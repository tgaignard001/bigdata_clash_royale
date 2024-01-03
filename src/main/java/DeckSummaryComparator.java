import scala.Tuple2;
import java.io.Serializable;

@FunctionalInterface
interface DeckSummaryComparator extends Serializable {
    int compare(Tuple2<String, DeckSummary> tuple1, Tuple2<String, DeckSummary> tuple2);
}


