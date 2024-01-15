package bigdata;

public class WinRateChecker implements IChecker{

    public WinRateChecker(){
    }
    @Override
    public boolean checkDeck(DeckSummary deckSummary) {
        int MIN_UNIQUE_PLAYER = 1;
        int MIN_USES = 1;
        return deckSummary.uses > MIN_USES && deckSummary.uniquePlayers > MIN_UNIQUE_PLAYER;
    }

    @Override
    public double getValue(DeckSummary deckSummary) {
        return (double) deckSummary.victories / deckSummary.uses;
    }
}
