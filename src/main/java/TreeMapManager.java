import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapManager {
    public static final int KVALUE = 500;

    public static void maintainTreeMapSize(TreeMap<Double, DeckSummaryWritable> treeMap) {
        while (treeMap.size() > KVALUE) {
            treeMap.remove(treeMap.firstKey());
        }
    }

    public static void sendSummaryFromTreeMaps(TaskInputOutputContext<?, ?, NullWritable, DeckSummaryWritable> context, TreeMap<Double, DeckSummaryWritable> treeMap) throws IOException, InterruptedException {
        for (Map.Entry<Double, DeckSummaryWritable> pair : treeMap.entrySet()) {
            context.write(NullWritable.get(), pair.getValue().clone());
        }
    }


    static void addYearWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck) {
        if (TopKChecker.checkYear(deck) && TopKChecker.checkWinRate(deck)) {
            double winRate = (double) deck.getVictories() / deck.getUses();
            treeMap.put(winRate, deck.clone());
        }
    }

    static void addMonthWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck) {
        if (TopKChecker.checkMonth(deck) && TopKChecker.checkWinRate(deck)) {
            double winRate = (double) deck.getVictories() / deck.getUses();
            treeMap.put(winRate, deck.clone());
        }
    }

    public interface DeckAdder {
        void addDeck(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck);
    }

    public static DeckAdder deckAdder = TreeMapManager::addMonthWinRate;
}
