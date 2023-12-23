import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapManager {
    public static void maintainTreeMapSize(TreeMap<Double, String> treeMap, int k){
        while(treeMap.size() > k){
            treeMap.remove(treeMap.firstKey());
        }
    }

    public static void sendSummaryFromTreeMaps(TaskInputOutputContext<?,?, NullWritable, SummaryTopK> context, TreeMap<Double, String> winRateTopK, TreeMap<Double, String> meanDiffForceTopK) throws IOException, InterruptedException {
        DeckTopK winRate = new DeckTopK();
        DeckTopK meanDiffForce = new DeckTopK();
        SummaryTopK output = new SummaryTopK();

        Iterator<Map.Entry<Double, String>> winRateIterator = winRateTopK.entrySet().iterator();
        Iterator<Map.Entry<Double, String>> meanDiffForceIterator = meanDiffForceTopK.entrySet().iterator();

        while (winRateIterator.hasNext() && meanDiffForceIterator.hasNext()){
            Map.Entry<Double, String> winRateEntry = winRateIterator.next();
            Map.Entry<Double, String> meanDiffForceEntry = meanDiffForceIterator.next();

            winRate.setId(winRateEntry.getValue());
            winRate.setValue(winRateEntry.getKey());

            meanDiffForce.setId(meanDiffForceEntry.getValue());
            meanDiffForce.setValue(meanDiffForceEntry.getKey());

            output.setValues(winRate.clone(), meanDiffForce.clone());
            context.write(NullWritable.get(), output);
        }
    }
}
