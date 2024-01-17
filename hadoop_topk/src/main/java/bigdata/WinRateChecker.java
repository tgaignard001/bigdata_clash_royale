package bigdata;

public class WinRateChecker implements IChecker{

    public WinRateChecker(){
    }
    @Override
    public boolean checkDeck(DeckSummary deckSummary) {
        int MIN_UNIQUE_PLAYER = 50;
        int MIN_USES = 100;
        return deckSummary.uses > MIN_USES && deckSummary.uniquePlayers > MIN_UNIQUE_PLAYER;
    }

    @Override
    public double getValue(DeckSummary deckSummary) {
        if (deckSummary.uses == 0) return 0;
        return (double) deckSummary.victories / deckSummary.uses;
    }
}
