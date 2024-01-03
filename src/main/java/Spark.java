import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;
import java.util.stream.Collectors;

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

        /*
        String outputDir = "result_summary";
        Path outputPath = new Path(outputDir);
        FileSystem fs = FileSystem.get(context.hadoopConfiguration());

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true); // Le deuxième paramètre indique la suppression récursive
        }*/
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

        for (Tuple2<String, DeckSummary> tuple : winRateList) {
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


        /*
        Dataset<Row> df = spark.createDataFrame(rowRDD, schema);

        Dataset<Row> dfConcatenated = df.withColumn("value_concatenated", functions.concat(df.col("originalValue"), functions.lit(","), df.col("count")));

        dfConcatenated = dfConcatenated.drop("originalValue").drop("count");

        dfConcatenated.write()
                .partitionBy("key")
                .text("output_directory");
        */
        /*
        JavaPairRDD<String, Iterable<Integer>> output = ngramsRDD.reduceByKey(
                entry -> {

                }
        );


        JavaPairRDD<String, NGramsRecord> nGramsRecordJavaPairRDD = ngramsCount.aggregateByKey(
                new NGramsRecord(),
                (record, value) -> {
                    // Extract the filename from the key
                    String fileName = NGrams.getFileNameFromKey(record.getKey());

                    // Add the value to the NGramsRecord instance
                    record.put(fileName, value);

                    return record;
                },
                (record1, record2) -> {
                    for (Map.Entry<String, Integer> entry : record2.getEntySet()) {
                        record1.put(entry.getKey(), entry.getValue());
                    }
                    return record1;
                }
        );*/
        /*

        ngramsCount.foreach(
                (entry) -> {
                    String filePath = "output_path/key_format_" + NGrams.getFileNameFromKey(entry._1);
                }
        );
        String ngram = "0c0f";
        SummaryDateType timeGranularity = SummaryDateType.MONTHLY;


        JavaPairRDD<String, Integer> ngramsResult = ngramsCount.filter(
                (entry) -> NGrams.filterKey(entry._1, ngram, timeGranularity)
        );
        */
    }


}
