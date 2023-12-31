public class TopKChecker {

    public static boolean checkMonth(DeckSummary deck){
        final long year = 2023;
        final long month = 11;
        return  deck.getDateType() == SummaryDateType.MONTHLY && deck.getYear() == year && deck.getMonth() == month;
    }

    public static boolean checkYear(DeckSummary deck){
        final long year = 2022;
        return deck.getDateType() == SummaryDateType.YEARLY && deck.getYear() == year;
    }

    public static boolean checkWinRate(DeckSummary deck) {
        final long MIN_USES = 100;
        final long MIN_UNIQUE_PLAYERS = 10;
        double uses = deck.getUses();
        long uniquePlayers = deck.getUniquePlayers();
        return uses > MIN_USES && uniquePlayers > MIN_UNIQUE_PLAYERS;
    }
}
