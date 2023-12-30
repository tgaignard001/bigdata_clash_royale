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
    private final PlayerInfoWritable player1;
    private final PlayerInfoWritable player2;

    public SummaryCreator(PlayerInfoWritable player1, PlayerInfoWritable player2, Instant date, long win) {
        this.player1 = player1.clone();
        this.player2 = player2.clone();
        this.diffForce = player1.getDeck() - player2.getDeck();
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.of("UTC"));
        this.year = zonedDateTime.getYear();
        this.month = zonedDateTime.getMonth();
        this.win = win;
    }

    public ArrayList<DeckSummaryWritable> generateSummaries() {
        ArrayList<DeckSummaryWritable> summaryList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            DeckSummaryWritable deckSummary1 = new DeckSummaryWritable(player1.getCards(), year, month.getValue(), dateType);
            DeckSummaryWritable deckSummary2 = new DeckSummaryWritable(player2.getCards(), year, month.getValue(), dateType);

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

    public ArrayList<UniquePlayerWritable> generateUniquePlayers() {
        ArrayList<UniquePlayerWritable> uniquePlayerList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            UniquePlayerWritable uniquePlayer1 = new UniquePlayerWritable(player1.getPlayer(), player1.getCards(), year, month.getValue(), dateType);
            UniquePlayerWritable uniquePlayer2 = new UniquePlayerWritable(player2.getPlayer(), player2.getCards(), year, month.getValue(), dateType);

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
                return sortedCards + "-" + year + "/" + ((month < 10) ? 0 : "") + month;
        }
        return sortedCards;
    }

    public static Matcher getKeyMatcher(String key) {
        Pattern pattern = Pattern.compile("(\\w+)(?:-([0-9]{4}))?(?:/([0-9]{2}))?");
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


