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

    final private static long MIN_USES = 100;
    final private static long MIN_UNIQUE_PLAYERS = 10;
    /**
     * Add in winRateTopk tree the winRate if the value is interesting
     *
     * @param treeMap TreeMap with value of winRate sorted
     * @param deck    DeckSummary with information of the deck
     */
    static void addWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck) {
        double victories = deck.getVictories();
        double uses = deck.getUses();
        long uniquePlayers = deck.getUniquePlayers();
        if (uses > MIN_USES && uniquePlayers > MIN_UNIQUE_PLAYERS && deck.getDateType() == SummaryDateType.NONE) {
            double winRate = victories / uses;
            treeMap.put(winRate, deck.clone());
        }
    }

    static void addYearWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck) {
        final long year = 2023;
        double uses = deck.getUses();
        long uniquePlayers = deck.getUniquePlayers();
        if (uses > MIN_USES && uniquePlayers > MIN_UNIQUE_PLAYERS && deck.getDateType() == SummaryDateType.YEARLY && deck.getYear() == year) {
            double winRate = deck.getVictories() / deck.getUses();
            treeMap.put(winRate, deck.clone());
        }
    }

    static void addMonthWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck) {
        final long year = 2023;
        final long month = 11;
        double uses = deck.getUses();
        long uniquePlayers = deck.getUniquePlayers();
        if (uses > MIN_USES && uniquePlayers > MIN_UNIQUE_PLAYERS && deck.getDateType() == SummaryDateType.MONTHLY && deck.getYear() == year && deck.getMonth() == month) {
            double winRate = deck.getVictories() / deck.getUses();
            treeMap.put(winRate, deck.clone());
        }
    }

    public interface DeckAdder {
        void addDeck(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck);
    }

    public static DeckAdder deckAdder = TreeMapManager::addMonthWinRate;
}
