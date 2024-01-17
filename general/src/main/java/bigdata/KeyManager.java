package bigdata;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyManager {
    public static String generateKey(String cards, SummaryDateType dateType, Instant date) {
        String sortedCards = InputFields.sortCards(cards);
        long month = DateManager.getMonth(date);
        long week = DateManager.getWeek(date);
        long year = DateManager.getYear(date);
        String cardsDash = ((sortedCards.isEmpty()) ? "" : "-");
        switch (dateType) {
            case NONE:
                return sortedCards + cardsDash + "N";
            case MONTHLY:
                return sortedCards + cardsDash + "M" + year + "-" + ((month < 10) ? "0" : "" ) + month;
            case WEEKLY:
                return sortedCards + cardsDash + "W" + year + "-" + ((week < 10) ? "0" : "") + week;
        }
        return sortedCards;
    }


    public static DeckSummary generateSummaryFromKey(String key) {
        SummaryDateType dateType = extractDateTypeFromKey(key);
        Instant date = extractDateFromKey(key);
        String cards = extractCardsFromKey(key);
        return new DeckSummary(cards, date, dateType);
    }

    public static Matcher getKeyMatcher(String key) {
        Pattern pattern = Pattern.compile("((\\w+)-)?([MNW])((\\d{4})?-(\\d{2}))?");
        return pattern.matcher(key);
    }

    public static String extractCardsFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches())
            if (matcher.group(2) != null) return matcher.group(2);
        return "0000000000000000";
    }

    public static Instant extractDateFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        SummaryDateType dateType = extractDateTypeFromKey(key);
        String year = "-1";
        String monthOrWeek = "01";
        if (matcher.matches()){
            if (matcher.group(5) != null) year = matcher.group(5);
            if (matcher.group(6) != null) monthOrWeek = matcher.group(6);
        }
        switch (dateType){
            case MONTHLY:
                return DateManager.getDateFromMonth(Integer.parseInt(year), Integer.parseInt(monthOrWeek));
            case WEEKLY:
                return DateManager.getDateFromWeek(Integer.parseInt(year), Integer.parseInt(monthOrWeek));
            default:
                return Instant.MIN;
        }
    }

    public static SummaryDateType extractDateTypeFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            String value = matcher.group(3);
            if (value != null && value.equals("M")) {
                return SummaryDateType.MONTHLY;
            } else if ((value != null && value.equals("W"))) {
                return SummaryDateType.WEEKLY;
            }
        }
        return SummaryDateType.NONE;
    }

/*
    public static void main(String[] args) {
        String key = "00010203050a585d-W2023-10";
        //121b212a3c4c5c62
        key = generateKey("", SummaryDateType.WEEKLY, Instant.now());
        System.out.println(generateKey("", SummaryDateType.NONE, Instant.now()));
        System.out.println("key: " + key);
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            System.out.println("Matched the pattern");
        }
        String cards = extractCardsFromKey(key);
        System.out.println(cards);
        Instant date = extractDateFromKey(key);
        System.out.println(date);
        SummaryDateType dateType = extractDateTypeFromKey(key);
        System.out.println(dateType);
    }
*/
}
