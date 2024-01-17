package bigdata;

public class MeanDiffForceChecker implements IChecker{

    public MeanDiffForceChecker(){
    }
    @Override
    public boolean checkDeck(DeckSummary deckSummary) {
        int MIN_UNIQUE_PLAYER = 50;
        int MIN_USES = 100;
        return deckSummary.uses > MIN_USES && deckSummary.uniquePlayers > MIN_UNIQUE_PLAYER;
    }

    @Override
    public double getValue(DeckSummary deckSummary) {
        if (deckSummary.nbDiffForce == 0) return 0;
        return deckSummary.sumDiffForce / deckSummary.nbDiffForce;
    }
}
