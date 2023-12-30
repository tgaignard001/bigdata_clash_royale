import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Objects;

public class Spark {

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
        System.out.println("count: ");
        long nbGames = gamesRDD.count();
        System.out.println("Nb games after filter : " + nbGames);
        for(Tuple2<String, Game> x : gamesRDD.collect()) {
            System.out.println(x._2.toString());
        }

        // Third part : cleaning
        JavaRDD<Tuple2<String, DeckSummaryWritable>> deckSummary = gamesRDD.map(
                (game) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode gameJson = objectMapper.readTree(jsonLine);
                    return null;
                }
        )

    }

}
