package bigdata;

import java.time.Instant;
import java.util.*;

public class TreeMapManager {
    private final int K_VALUE = 500;
    private final HashMap<String, TreeMap<Double, DeckSummary>> treeList;
    private final IChecker checker;

    Comparator<Double> comparator = (Double key1, Double key2) -> {
        int result = Double.compare(key1, key2);
        return (result != 0) ? result : 1;
    };
    public TreeMapManager() {
        this.treeList = new HashMap<>();
        this.treeList.put(KeyManager.generateKey("", SummaryDateType.NONE, Instant.now()), new TreeMap<>(comparator));
        this.checker = new WinRateChecker();
    }

    public void maintainTreeSize(TreeMap<Double, DeckSummary> tree) {
        while (tree.size() > K_VALUE) {
            tree.remove(tree.firstKey());
        }
    }

    public void addNewDeck(DeckSummary deckSummary) {
        String tree_key = KeyManager.generateKey("", deckSummary.dateType, deckSummary.date);
        TreeMap<Double, DeckSummary> tree = treeList.computeIfAbsent(tree_key, k -> new TreeMap<>(comparator));
        if (checker.checkDeck(deckSummary)) {
            tree.put(checker.getValue(deckSummary), deckSummary.clone());
            maintainTreeSize(tree);
        }
    }

    public HashMap<String, TreeMap<Double, DeckSummary>> getTreeList() {
        return treeList;
    }


    public ArrayList<DeckSummary> getTopKLine(){
        ArrayList<DeckSummary> topKLine = new ArrayList<>();
        boolean isEmpty = true;
        for (TreeMap<Double, DeckSummary> tree: treeList.values()){
            Map.Entry<Double, DeckSummary> pair = tree.pollFirstEntry();
            if (pair != null){
                topKLine.add(pair.getValue());
                isEmpty = false;
            }else{
                topKLine.add(new DeckSummary("0000000000000000", Instant.now(), SummaryDateType.NONE));
            }
        }
        if (!isEmpty) return topKLine;
        return null;
    }

}
