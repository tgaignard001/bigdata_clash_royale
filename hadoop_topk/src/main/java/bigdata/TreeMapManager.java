package bigdata;

import java.time.Instant;
import java.util.*;

public class TreeMapManager {
    private final int K_VALUE = 500;
    private final HashMap<String, TreeMap<String, DeckSummary>> treeList;
    private final IChecker checker;

    Comparator<String> comparator = (String key1, String key2) -> {
        String[] parts1 = key1.split("_");
        String[] parts2 = key2.split("_");
        double value1 = Double.parseDouble(parts1[0]);
        double value2 = Double.parseDouble(parts2[0]);
        int result = Double.compare(value1, value2);
        return (result != 0) ? result : parts1[1].compareTo(parts2[1]);
    };
    public TreeMapManager() {
        this.treeList = new HashMap<>();
        this.treeList.put(KeyManager.generateKey("", SummaryDateType.NONE, Instant.now()), new TreeMap<>(comparator));
        this.checker = new WinRateChecker();
    }

    public void maintainTreeSize(TreeMap<String, DeckSummary> tree) {
        while (tree.size() > K_VALUE) {
            DeckSummary result = tree.remove(tree.firstKey());
            System.out.println(tree.firstKey() + result);
        }
    }

    public void addNewDeck(DeckSummary deckSummary) {
        String tree_key = KeyManager.generateKey("", deckSummary.dateType, deckSummary.date);
        TreeMap<String, DeckSummary> tree = treeList.computeIfAbsent(tree_key, k -> new TreeMap<>(comparator));
        if (checker.checkDeck(deckSummary)) {
            tree.put(checker.getValue(deckSummary) + "_" + deckSummary.sortedCards, deckSummary.clone());
            maintainTreeSize(tree);
        }
    }

    public HashMap<String, TreeMap<String, DeckSummary>> getTreeList() {
        return treeList;
    }


    public ArrayList<DeckSummary> getTopKLine(){
        ArrayList<DeckSummary> topKLine = new ArrayList<>();
        boolean isEmpty = true;
        for (Map.Entry<String, TreeMap<String, DeckSummary>> tree: treeList.entrySet()){
            Map.Entry<String, DeckSummary> pair = tree.getValue().pollLastEntry();
            String tree_key = tree.getKey();
            if (pair != null){
                topKLine.add(pair.getValue());
                isEmpty = false;
            }else{
                topKLine.add(new DeckSummary("0000000000000000", KeyManager.extractDateFromKey(tree_key), KeyManager.extractDateTypeFromKey(tree_key)));
            }
        }
        if (!isEmpty) return topKLine;
        return null;
    }



}
