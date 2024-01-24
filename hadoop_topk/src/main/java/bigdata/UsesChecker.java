package bigdata;

public class UsesChecker implements IChecker{

    public UsesChecker(){
    }
    @Override
    public boolean checkDeck(DeckSummary deckSummary) {
        int MIN_UNIQUE_PLAYER = 10;
        int MIN_USES = 100;
        return deckSummary.uses > MIN_USES && deckSummary.uniquePlayers > MIN_UNIQUE_PLAYER;
    }

    @Override
    public double getValue(DeckSummary deckSummary) {
        return deckSummary.uses;
    }
}
