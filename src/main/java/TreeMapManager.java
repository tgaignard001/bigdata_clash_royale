import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapManager {
    public static void maintainTreeMapSize(TreeMap<Double, DeckSummaryWritable> treeMap, int k){
        while(treeMap.size() > k){
            treeMap.remove(treeMap.firstKey());
        }
    }

    public static void sendSummaryFromTreeMaps(TaskInputOutputContext<?,?, NullWritable, DeckSummaryWritable> context, TreeMap<Double, DeckSummaryWritable> treeMap) throws IOException, InterruptedException {for(Map.Entry<Double, DeckSummaryWritable> pair : treeMap.entrySet()) {
            context.write(NullWritable.get(), pair.getValue().clone());
        }
    }

    /**
     * Add in winRateTopk tree the winRate if the value is interesting
     * @param treeMap TreeMap with value of winRate sorted
     * @param deck DeckSummary with information of the deck
     */
    public static void addWinRate(TreeMap<Double, DeckSummaryWritable> treeMap, DeckSummaryWritable deck){
        double victories = deck.getVictories();
        double uses = deck.getUses();
        if (uses > 100) {
            double winRate = victories/uses;
            treeMap.put(winRate, deck.clone());
        }
    }
}
