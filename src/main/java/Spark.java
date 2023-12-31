import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.*;

public class Spark {

    private static final int K = 5;

    public static void main(String[] args) throws Exception {
        final SparkConf sparkConf = new SparkConf().setAppName("SparkMapReduceClashRoyale");
        final JavaSparkContext context = new JavaSparkContext(sparkConf);

        // First part to get data
        JavaRDD<String> rawRDD = context.textFile(args[0],2);

        int nbPartitions = rawRDD.getNumPartitions();
        System.out.println("Nb partitions : " + nbPartitions); // default : 2

        // Second part : cleaning
        JavaRDD<Tuple2<String, Game>> gamesRDD = rawRDD.map(
                (jsonLine) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode gameJson = objectMapper.readTree(jsonLine);
                    if (InputFields.checkFields(gameJson)) {
                        Game game = new Game(InputFields.createGame(gameJson));
                        return new Tuple2<>(game.getId(), game);
                    }
                    return null;
                }
        ).filter(Objects::nonNull);
        gamesRDD = gamesRDD.distinct();
        long nbGames = gamesRDD.count();
        System.out.println("Nb games after filter : " + nbGames);
        // Third part : cleaning

        // Map DeckSummary
        JavaPairRDD<String, DeckSummary> deckSummaries = gamesRDD.flatMapToPair(
                entry -> {
                    Game game = entry._2;
                    SummaryCreator summaryCreator = new SummaryCreator(game.getPlayer1(), game.getPlayer2(), game.getDate(), game.getWin());

                    List<Tuple2<String, DeckSummary>> result = new ArrayList<>();
                    for (DeckSummary deckSummary : summaryCreator.generateSummaries()) {
                        String deckSummaryKey = SummaryCreator.generateKey(deckSummary.getSortedCards(), deckSummary.getDateType(), deckSummary.getYear(), deckSummary.getMonth());
                        result.add(new Tuple2<>(deckSummaryKey, deckSummary));
                    }
                    return result.iterator();
                }
        ).distinct();

        JavaPairRDD<String, Iterable<DeckSummary>> deckSummaryGroupedByKey = deckSummaries.groupByKey();
        System.out.println("Nb decks after filer : " + deckSummaryGroupedByKey.count());

        // Map Unique Player
        JavaPairRDD<String, UniquePlayer> deckWithPlayerPairRDD = gamesRDD.flatMapToPair(
                entry -> {
                    Game game = entry._2;
                    SummaryCreator summaryCreator = new SummaryCreator(game.getPlayer1(), game.getPlayer2(), game.getDate(), game.getWin());
                    ArrayList<UniquePlayer> uniquePlayers = summaryCreator.generateUniquePlayers();

                    List<Tuple2<String, UniquePlayer>> result = new ArrayList<>();
                    for (UniquePlayer uniquePlayer : uniquePlayers) {
                        String uniquePlayerKey = SummaryCreator.generateKey(uniquePlayer.getCards(), uniquePlayer.getDateType(), uniquePlayer.getYear(), uniquePlayer.getMonth());
                        result.add(new Tuple2<>(uniquePlayerKey, uniquePlayer));
                    }

                    return result.iterator();
                }
        );

        JavaPairRDD<String, DeckSummary> deckWithNumberUniquePlayerRDD = deckWithPlayerPairRDD
                .groupByKey()
                .mapToPair(tuple -> {
                    String key = tuple._1();
                    Iterable<UniquePlayer> iterable = tuple._2();

                    Set<String> uniquePlayersSet = new HashSet<>();
                    for (UniquePlayer uniquePlayer : iterable) {
                        uniquePlayersSet.add(uniquePlayer.playerName);
                    }
                    return new Tuple2<>(key, SummaryCreator.generateSummaryFromKeyAndUniquePlayersCount(key, uniquePlayersSet.size()));
                });


        // Merge Unique Players and DeckSummary
        JavaPairRDD<String, Tuple2<Iterable<DeckSummary>, DeckSummary>> joinedRDD = deckSummaryGroupedByKey.join(deckWithNumberUniquePlayerRDD);
        JavaPairRDD<String, DeckSummary> summaryRDD = joinedRDD.mapToPair(
                (value) -> {
                    String key = value._1();
                    Tuple2<Iterable<DeckSummary>, DeckSummary> iterable = value._2();
                    DeckSummary deckSummary = iterable._2();

                    for (DeckSummary deckSummaryElement : iterable._1()) {
                        deckSummary.updateDeckSummary(deckSummaryElement);
                    }

                    return new Tuple2<>(key, deckSummary.clone());
                }
        );

        String outputDir = "result_summary";
        Path outputPath = new Path(outputDir);
        FileSystem fs = FileSystem.get(context.hadoopConfiguration());

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true); // Le deuxième paramètre indique la suppression récursive
        }
        summaryRDD.saveAsTextFile("result_summary");

        // TopK
        List<Tuple2<String, DeckSummary>> winRateList = summaryRDD.filter(
                        (tuple) -> TopKChecker.checkMonth(tuple._2) && TopKChecker.checkWinRate(tuple._2)
                ).takeOrdered(K,
                new TopKComparator(
                        (tuple1, tuple2) ->
                                Long.compare(tuple2._2().getVictories(), tuple1._2().getVictories())
                )
        );

        for (Tuple2<String, DeckSummary> tuple : winRateList){
            System.out.println(tuple._2);
        }

    }


}
