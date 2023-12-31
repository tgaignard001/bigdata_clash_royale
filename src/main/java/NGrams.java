import java.util.ArrayList;
import java.util.List;

class NGrams {

    private static List<String> combinationUtil(ArrayList<String> arr, ArrayList<String> data, int start,
                                        int end, int index, int r) {
        List<String> result = new ArrayList<>();

        if (index == r) {
            StringBuilder combination = new StringBuilder();

            for (int j = 0; j < r; j++) {
                combination.append(data.get(j));
            }
            result.add(combination.toString().trim());
            return result;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data.set(index, arr.get(i));
            result.addAll(combinationUtil(arr, data, i + 1, end, index + 1, r));
        }
        return result;
    }

    private static List<String> generateCombinations(ArrayList<String> arr, int r) {
        ArrayList<String> data = new ArrayList<>(r);
        for (int i = 0; i < r; i++) {
            data.add("");
        }

        return combinationUtil(arr, data, 0, arr.size() - 1, 0, r);
    }

    private static ArrayList<String> convertStringToArrayList(String str) {
        ArrayList<String> cardList = new ArrayList<>();
        for (int i = 0; i < str.length() / 2; ++i) {
            String card = str.substring(i * 2, i * 2 + 2);
            cardList.add(card);
        }
        return cardList;
    }

    public static ArrayList<String> generateKeyCombination(String combination)
    {
        ArrayList<String> arr = convertStringToArrayList(combination);
        ArrayList<String> combinations = new ArrayList<>();
        for (int i = 2; i <= combination.length()/2; ++i){
            combinations.addAll(generateCombinations(arr, i));
        }
        return combinations;
    }

    public static String generateNgramsOutputKey(String key){
        if (SummaryCreator.extractDateTypeFromKey(key) == SummaryDateType.NONE) {
            return SummaryCreator.extractCardsFromKey(key);
        } else if (SummaryCreator.extractDateTypeFromKey(key) == SummaryDateType.MONTHLY){
            return SummaryCreator.extractCardsFromKey(key) + "_MONTH";
        } else {
            return SummaryCreator.extractCardsFromKey(key) + "_YEAR";
        }
    }

    private static String generateNgramsKey(String key){
        String cards = SummaryCreator.extractCardsFromKey(key);
        if (SummaryCreator.extractDateTypeFromKey(key) == SummaryDateType.NONE) {
            return cards;
        } else if (SummaryCreator.extractDateTypeFromKey(key) == SummaryDateType.MONTHLY){
            return cards + "_MONTH";
        } else {
            return cards + "_YEAR";
        }
    }
    public static ArrayList<String> generateKeys(String summaryKey){
        ArrayList<String> keys = NGrams.generateKeyCombination(SummaryCreator.extractCardsFromKey(summaryKey));
        ArrayList<String> ouputKeys = new ArrayList<String>();
        for (String key : keys){
            ouputKeys.add(SummaryCreator.generateKey(key, SummaryCreator.extractDateTypeFromKey(summaryKey), SummaryCreator.extractYearFromKey(summaryKey), SummaryCreator.extractMonthFromKey(summaryKey)));
        }
        return ouputKeys;
    }


    public static boolean filterKey(String key, String ngrams, SummaryDateType dateType){
        return SummaryCreator.extractCardsFromKey(key).equals(ngrams) && SummaryCreator.extractDateTypeFromKey(key) == dateType;
    }

    public static String getFileNameFromKey(String key){
        String ngram = SummaryCreator.extractCardsFromKey(key);
        SummaryDateType summaryDateType = SummaryCreator.extractDateTypeFromKey(key);
        return ngram + "-" + summaryDateType;
    }

    public static void main(String[] args) {
        String arr = "0c0f1a2a31373e40";
        List<String> combinations = generateKeys(arr);

        for (String combination : combinations) {
            System.out.println(combination);
        }
    }
}
