package bigdata;

import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryCreator {

    PlayerInfoWritable player1;
    PlayerInfoWritable player2;
    Instant date;
    boolean isPlayer1Win;
    double diffForce;

    public SummaryCreator(PlayerInfoWritable player1, PlayerInfoWritable player2, Instant date, long win) {
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.isPlayer1Win = win == 1;
        this.diffForce = player1.deck - player2.deck;
    }

    public ArrayList<DeckSummary> generateSummaries() {
        ArrayList<DeckSummary> summaryList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            DeckSummary deckSummary1 = new DeckSummary(player1.cards, date, dateType);
            DeckSummary deckSummary2 = new DeckSummary(player2.cards, date, dateType);

            if (isPlayer1Win) {
                deckSummary1.incVictories();
            } else {
                deckSummary2.incVictories();
            }

            deckSummary1.incUses();
            deckSummary2.incUses();

            deckSummary1.setHighestClanLevel(player1.clanTr);
            deckSummary2.setHighestClanLevel(player2.clanTr);

            deckSummary1.addDiffForce(diffForce);
            deckSummary2.addDiffForce(-diffForce);

            deckSummary1.incNbDiffForce();
            deckSummary2.incNbDiffForce();

            summaryList.add(deckSummary1);
            summaryList.add(deckSummary2);
        }
        return summaryList;
    }

    public static String generateKey(String cards, SummaryDateType dateType, Instant date) {
        String sortedCards = InputFields.sortCards(cards);
        switch (dateType) {
            case NONE:
                return sortedCards + "-N" + date.toString();
            case MONTHLY:
                return sortedCards + "-M" + date.toString();
            case WEEKLY:
                return sortedCards + "-W" + date.toString();
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
        Pattern pattern = Pattern.compile("(\\w+)-([MNW])(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)");
        return pattern.matcher(key);
    }

    public static String extractCardsFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            // not possible
            return "0000000000000000";
        }
    }

    public static Instant extractDateFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches() && matcher.group(3) != null) {
            return Instant.parse(matcher.group(3));
        } else {
            return Instant.MIN;
        }
    }

    public static SummaryDateType extractDateTypeFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            if (matcher.group(2) != null && matcher.group(2).equals("M")) {
                return SummaryDateType.MONTHLY;
            } else if ((matcher.group(2) != null && matcher.group(2).equals("W"))) {
                return SummaryDateType.WEEKLY;
            }
        }
        return SummaryDateType.NONE;
    }

    public static void main(String[] args) {
        String key = "00010203050a585d-W2023-10-02T05:54:07Z";//SummaryCreator.generateKey("121b212a3c4c5c62", SummaryDateType.NONE, Instant.now());
        System.out.println("key: " + key);
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            System.out.println("Group matchers: " + matcher.group());
        }
        String cards = extractCardsFromKey(key);
        System.out.println(cards);
        Instant date = extractDateFromKey(key);
        System.out.println(date);
        SummaryDateType dateType = extractDateTypeFromKey(key);
        System.out.println(dateType);
    }
}
