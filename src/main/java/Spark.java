import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.StatCounter;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Spark {

    public static void main(String[] args) throws Exception {
        final SparkConf sparkConf = new SparkConf().setAppName("SparkPi");
        final JavaSparkContext context = new JavaSparkContext(sparkConf);

        JavaRDD<String> worldRDD = context.textFile(args[0],2);

        int nbPartitions = worldRDD.getNumPartitions();
        System.out.println("Nb partitions : " + nbPartitions); // default : 2

    }

}
