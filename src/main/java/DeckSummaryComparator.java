import scala.Tuple2;
import java.io.Serializable;

@FunctionalInterface
interface DeckSummaryComparator extends Serializable {
    int compare(Tuple2<String, DeckSummaryWritable> tuple1, Tuple2<String, DeckSummaryWritable> tuple2);
}


