import scala.Serializable;
import scala.Tuple2;

import java.util.Comparator;

public class TopKComparator implements Comparator<Tuple2<String, DeckSummaryWritable>>, Serializable {
    private final DeckSummaryComparator comparator;

    public TopKComparator(DeckSummaryComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(Tuple2<String, DeckSummaryWritable> tuple1, Tuple2<String, DeckSummaryWritable> tuple2) {
        return comparator.compare(tuple1, tuple2);
    }

    // Example of using a lambda function
    public static void main(String[] args) {
        TopKComparator topKComparator = new TopKComparator(
                (tuple1, tuple2) -> Long.compare(tuple2._2().getVictories(), tuple1._2().getVictories())
        );
    }
}
