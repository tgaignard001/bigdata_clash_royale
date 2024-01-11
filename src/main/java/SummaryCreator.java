import scala.collection.parallel.ParIterableLike;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryCreator {
    private final double diffForce;
    private final long year;
    private final Month month;
    private final long win;
    private final PlayerInfo player1;
    private final PlayerInfo player2;

    public SummaryCreator(PlayerInfo player1, PlayerInfo player2, Instant date, long win) {
        this.player1 = player1.clone();
        this.player2 = player2.clone();
        this.diffForce = player1.getDeck() - player2.getDeck();
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.of("UTC"));
        this.year = zonedDateTime.getYear();
        this.month = zonedDateTime.getMonth();
        this.win = win;
    }

    public ArrayList<DeckSummary> generateSummaries() {
        ArrayList<DeckSummary> summaryList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            DeckSummary deckSummary1 = new DeckSummary(player1.getCards(), year, month.getValue(), dateType);
            DeckSummary deckSummary2 = new DeckSummary(player2.getCards(), year, month.getValue(), dateType);

            if (win == 1) {
                deckSummary1.incVictories();
            } else {
                deckSummary2.incVictories();
            }

            deckSummary1.incUses();
            deckSummary2.incUses();

            deckSummary1.setHighestClanLevel(player1.getClanTr());
            deckSummary2.setHighestClanLevel(player2.getClanTr());

            deckSummary1.addDiffForce(diffForce);
            deckSummary2.addDiffForce(-diffForce);

            deckSummary1.incNbDiffForce();
            deckSummary2.incNbDiffForce();

            summaryList.add(deckSummary1);
            summaryList.add(deckSummary2);
        }
        return summaryList;
    }

    public ArrayList<UniquePlayer> generateUniquePlayers() {
        ArrayList<UniquePlayer> uniquePlayerList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            UniquePlayer uniquePlayer1 = new UniquePlayer(player1.getPlayer(), player1.getCards(), year, month.getValue(), dateType);
            UniquePlayer uniquePlayer2 = new UniquePlayer(player2.getPlayer(), player2.getCards(), year, month.getValue(), dateType);

            uniquePlayerList.add(uniquePlayer1);
            uniquePlayerList.add(uniquePlayer2);
        }
        return uniquePlayerList;
    }

    public static String generateKey(String cards, SummaryDateType dateType, long year, long month) {
        String sortedCards = InputFields.sortCards(cards);
        switch (dateType) {
            case NONE:
                return sortedCards;
            case YEARLY:
                return sortedCards + "-" + year;
            case MONTHLY:
                return sortedCards + "-" + year + "_" + ((month < 10) ? 0 : "") + month;
        }
        return sortedCards;
    }

    public static DeckSummary generateSummaryFromKeyAndUniquePlayersCount(String key, long uniquePlayersCount){
        String cards = SummaryCreator.extractCardsFromKey(key);
        long year = SummaryCreator.extractYearFromKey(key);
        long month = SummaryCreator.extractMonthFromKey(key);
        SummaryDateType dateType = SummaryCreator.extractDateTypeFromKey(key);
        DeckSummaryWritable deckSummary = new DeckSummaryWritable(cards, year, month, dateType);
        deckSummary.setUniquePlayers(uniquePlayersCount);
        return deckSummary;
    }

    public static Matcher getKeyMatcher(String key) {
        Pattern pattern = Pattern.compile("(\\w+)(?:-([0-9]{4}))?(?:_((M | W)[0-9]{2}))?");
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

    public static long extractYearFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches() && matcher.group(2) != null) {
            return Long.parseLong(matcher.group(2));
        } else {
            return 0;
        }
    }

    public static long extractMonthFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches() && matcher.group(3) != null) {
            return Long.parseLong(matcher.group(3));
        } else {
            return 0;
        }
    }

    public static String extractDateFromKey(String key) {
        if (extractDateTypeFromKey(key) == SummaryDateType.NONE) {
            return "";
        } else if (extractDateTypeFromKey(key) == SummaryDateType.MONTHLY){
            return String.valueOf(extractMonthFromKey(key));
        } else {
            return String.valueOf(extractYearFromKey(key));
        }
    }

    public static SummaryDateType extractDateTypeFromKey(String key) {
        Matcher matcher = getKeyMatcher(key);
        if (matcher.matches()) {
            if (matcher.group(2) == null) {
                return SummaryDateType.NONE;
            } else if (matcher.group(3) == null) {
                return SummaryDateType.YEARLY;
            } else {
                return SummaryDateType.MONTHLY;
            }
        }
        return SummaryDateType.NONE;
    }

    public static void main(String[] args) {
        String key = SummaryCreator.generateKey("121b212a3c4c5c62", SummaryDateType.YEARLY, 2023, 10);
        System.out.println(key);
        String cards = extractCardsFromKey(key);
        System.out.println(cards);
        long year = extractYearFromKey(key);
        System.out.println(year);
        long month = extractMonthFromKey(key);
        System.out.println(month);
        SummaryDateType dateType = extractDateTypeFromKey(key);
        System.out.println(dateType);
    }

}


