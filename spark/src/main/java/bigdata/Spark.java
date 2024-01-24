package bigdata;

import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.w3c.dom.Text;
import scala.Tuple2;

import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Spark {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("NGramsSpark");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        String inputPath = args[0];
        JavaPairRDD<Text, DeckSummaryWritable> sequenceFileRDD = sparkContext.sequenceFile(inputPath, Text.class, DeckSummaryWritable.class);
        int nbPartitions = sequenceFileRDD.getNumPartitions();
        System.out.println("Nb partitions : " + nbPartitions);
        JavaPairRDD<Text, DeckSummaryWritable> filteredRDD = sequenceFileRDD.filter(
                entry -> (entry._2.deckSummary.dateType == SummaryDateType.WEEKLY)
        );

            JavaPairRDD<String, NgramSummary> ngramsRDD = filteredRDD.flatMapToPair(
                    entry -> {
                        DeckSummary deck = entry._2.deckSummary;
                        SummaryDateType dateType = deck.dateType;
                        List<String> ngrams = NGrams.generateKeyCombination(deck.sortedCards);
                        return ngrams.stream()
                                .map(ngram -> {
                                    String newKey = KeyManager.generateKey(ngram, dateType, deck.date);
                                    NgramSummary ngramSummary = new NgramSummary(ngram, deck.date, dateType);
                                    ngramSummary.updateFromDeckSummary(deck);
                                    return new Tuple2<>(newKey, ngramSummary.clone());
                                })
                                .iterator();
                    }
            );

        JavaPairRDD<String, NgramSummary> reducedNgramsRDD =
                ngramsRDD.reduceByKey((summary1, summary2) -> {
                    NgramSummary mergedSummary = summary1.clone();
                    mergedSummary.updateSummary(summary2.clone());
                    return mergedSummary;
                });

        JavaPairRDD<String, NgramSummary> sortedNgramsRDD = reducedNgramsRDD
                .sortByKey(true);

        sortedNgramsRDD
                .map(pair -> new Gson().toJson(pair))
                .saveAsTextFile(args[1]);


        sparkContext.close();
    }
}
