import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.*;

public class Spark {

    private static final int K = 5;

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("SparkMapReduceClashRoyale")
                .getOrCreate();

        Dataset<String> rawDataset = spark.read().textFile(args[0]);

        JavaRDD<String> rawRDD = rawDataset.javaRDD();

        int nbPartitions = rawRDD.getNumPartitions();
        System.out.println("Nb partitions : " + nbPartitions); // default : 2

        // Second part : cleaning
        JavaRDD<Tuple2<String, GameWritable>> gamesRDD = rawRDD.map(
                (jsonLine) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode gameJson = objectMapper.readTree(jsonLine);
                    if (InputFields.checkFields(gameJson)) {
                        GameWritable gameWritable = InputFields.createGame(gameJson);
                        return new Tuple2<>(gameWritable.getId(), gameWritable);
                    }
                    return null;
                }
        ).filter(Objects::nonNull);
        gamesRDD = gamesRDD.distinct();
        long nbGames = gamesRDD.count();
        System.out.println("Nb games after filter : " + nbGames);
        // Third part : cleaning

        // Map DeckSummary
        JavaPairRDD<String, DeckSummaryWritable> deckSummaries = gamesRDD.flatMapToPair(
                entry -> {
                    GameWritable gameWritable = entry._2;
                    SummaryCreator summaryCreator = new SummaryCreator(gameWritable.getPlayer1(), gameWritable.getPlayer2(), gameWritable.getDate(), gameWritable.getWin());

                    List<Tuple2<String, DeckSummaryWritable>> result = new ArrayList<>();
                    for (DeckSummaryWritable deckSummary : summaryCreator.generateSummaries()) {
                        String deckSummaryKey = SummaryCreator.generateKey(deckSummary.getSortedCards(), deckSummary.getDateType(), deckSummary.getYear(), deckSummary.getMonth());
                        result.add(new Tuple2<>(deckSummaryKey, deckSummary));
                    }
                    return result.iterator();
                }
        ).distinct();

        JavaPairRDD<String, Iterable<DeckSummaryWritable>> deckSummaryGroupedByKey = deckSummaries.groupByKey();
        System.out.println("Nb decks after filer : " + deckSummaryGroupedByKey.count());

        // Map Unique Player
        JavaPairRDD<String, UniquePlayerWritable> deckWithPlayerPairRDD = gamesRDD.flatMapToPair(
                entry -> {
                    GameWritable gameWritable = entry._2;
                    SummaryCreator summaryCreator = new SummaryCreator(gameWritable.getPlayer1(), gameWritable.getPlayer2(), gameWritable.getDate(), gameWritable.getWin());
                    ArrayList<UniquePlayerWritable> uniquePlayerWritables = summaryCreator.generateUniquePlayers();

                    List<Tuple2<String, UniquePlayerWritable>> result = new ArrayList<>();
                    for (UniquePlayerWritable uniquePlayer : uniquePlayerWritables) {
                        String uniquePlayerKey = SummaryCreator.generateKey(uniquePlayer.getCards(), uniquePlayer.getDateType(), uniquePlayer.getYear(), uniquePlayer.getMonth());
                        result.add(new Tuple2<>(uniquePlayerKey, uniquePlayer));
                    }

                    return result.iterator();
                }
        );

        JavaPairRDD<String, DeckSummaryWritable> deckWithNumberUniquePlayerRDD = deckWithPlayerPairRDD
                .groupByKey()
                .mapToPair(tuple -> {
                    String key = tuple._1();
                    Iterable<UniquePlayerWritable> iterable = tuple._2();

                    Set<String> uniquePlayersSet = new HashSet<>();
                    for (UniquePlayerWritable uniquePlayer : iterable) {
                        uniquePlayersSet.add(uniquePlayer.playerName);
                    }
                    return new Tuple2<>(key, SummaryCreator.generateSummaryFromKeyAndUniquePlayersCount(key, uniquePlayersSet.size()));
                });


        // Merge Unique Players and DeckSummary
        JavaPairRDD<String, Tuple2<Iterable<DeckSummaryWritable>, DeckSummaryWritable>> joinedRDD = deckSummaryGroupedByKey.join(deckWithNumberUniquePlayerRDD);
        JavaPairRDD<String, DeckSummaryWritable> summaryRDD = joinedRDD.mapToPair(
                (value) -> {
                    String key = value._1();
                    Tuple2<Iterable<DeckSummaryWritable>, DeckSummaryWritable> iterable = value._2();
                    DeckSummaryWritable deckSummaryWritable = iterable._2();

                    for (DeckSummaryWritable deckSummaryElement : iterable._1()) {
                        deckSummaryWritable.updateDeckSummary(deckSummaryElement);
                    }

                    return new Tuple2<>(key, deckSummaryWritable.clone());
                }
        );

        /*
        String outputDir = "result_summary";
        Path outputPath = new Path(outputDir);
        FileSystem fs = FileSystem.get(context.hadoopConfiguration());

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true); // Le deuxième paramètre indique la suppression récursive
        }*/
        summaryRDD.saveAsTextFile("result_summary");

        // TopK
        List<Tuple2<String, DeckSummaryWritable>> winRateList = summaryRDD.filter(
                (tuple) -> TopKChecker.checkMonth(tuple._2) && TopKChecker.checkWinRate(tuple._2)
        ).takeOrdered(K,
                new TopKComparator(
                        (tuple1, tuple2) ->
                                Long.compare(tuple2._2().getVictories(), tuple1._2().getVictories())
                )
        );

        for (Tuple2<String, DeckSummaryWritable> tuple : winRateList) {
            System.out.println(tuple._2);
        }


        // Spark n-gram
        //  For now, we currently assume that the data comes from summaryRDD; later on, we will read the classes from the sequence file.
        System.out.println("Start Ngrams");
        JavaPairRDD<String, Long> ngramsRDD = summaryRDD.flatMapToPair(
                entry -> {
                    ArrayList<String> keys = NGrams.generateKeys(entry._1());
                    List<Tuple2<String, Long>> result = new ArrayList<>();

                    for (String key : keys) {
                        result.add(new Tuple2<>(key, entry._2.getUses()));
                    }

                    return result.iterator();
                }
        );

        JavaPairRDD<String, Long> ngramsCount = ngramsRDD.aggregateByKey(
                0L,
                Long::sum,
                Long::sum
        );

        JavaPairRDD<String, Tuple2<String, Long>> ngramswByFileRDD = ngramsCount.mapToPair(
                entry -> {
                    System.out.println(entry._1);
                    System.out.println(NGrams.generateNgramsOutputKey(entry._1));
                    return new Tuple2<>(NGrams.generateNgramsOutputKey(entry._1), new Tuple2<>(entry._1, entry._2));
                }
        );

        System.out.println("Finish compute");
        System.out.println("Start convert");
        JavaRDD<Row> rowRDD = ngramswByFileRDD.map(tuple -> RowFactory.create(
                tuple._1,
                SummaryCreator.extractDateFromKey(tuple._2._1),
                String.valueOf(tuple._2._2)
        ));

        StructType schema = new StructType(new StructField[]{
                new StructField("key", DataTypes.StringType, false, Metadata.empty()),
                new StructField("date", DataTypes.StringType, false, Metadata.empty()),
                new StructField("count", DataTypes.StringType, false, Metadata.empty())
        });
        System.out.println("Start Ngrams");
        Dataset<Row> df = spark.createDataFrame(rowRDD, schema);
        System.out.println("Start limit");

        System.out.println("Start writing");
        df.write()
                .partitionBy("key")
                .json("ngrams_directory");
    }

}
