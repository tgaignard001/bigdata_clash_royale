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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Spark {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("NGramsSpark");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        String inputPath = args[0];
        JavaPairRDD<Text, DeckSummaryWritable> sequenceFileRDD = sparkContext.sequenceFile(inputPath, Text.class, DeckSummaryWritable.class);

        JavaPairRDD<String, NgramSummary> ngramsRDD = sequenceFileRDD.flatMapToPair(
                entry -> {
                    System.out.println(entry._1);
                    DeckSummary deck = entry._2.deckSummary;
                    String key = KeyManager.generateKey(deck.sortedCards, deck.dateType, deck.date);
                    ArrayList<String> ngrams = NGrams.generateKeyCombination(KeyManager.extractCardsFromKey(key));
                    SummaryDateType dateType = KeyManager.extractDateTypeFromKey(key);
                    Instant date = KeyManager.extractDateFromKey(key);
                    List<Tuple2<String, NgramSummary>> newNgramEntries = new ArrayList<>();
                    for (String ngram : ngrams){
                        String newKey = KeyManager.generateKey(ngram, dateType, date);
                        NgramSummary ngramSummary = new NgramSummary();
                        ngramSummary.updateFromDeckSummary(deck);
                        newNgramEntries.add(new Tuple2<>(newKey, ngramSummary.clone()));
                    }
                    return newNgramEntries.iterator();
                }
        );

        JavaPairRDD<String, NgramSummary> aggregatedNgramsRDD = ngramsRDD.aggregateByKey(
                new NgramSummary(),
                (ngramToUpdate, ngram) -> {
                    NgramSummary updatedSummary = ngramToUpdate.clone();
                    updatedSummary.updateSummary(ngram.clone());
                    return updatedSummary;
                },
                (ngram1, ngram2) -> {
                    NgramSummary mergedSummary = ngram1.clone();
                    mergedSummary.updateSummary(ngram2.clone());
                    return mergedSummary;
                }
        );

        aggregatedNgramsRDD
                .map(pair -> new Gson().toJson(pair))
                .saveAsTextFile(args[1]);


        sparkContext.close();
    }
}
