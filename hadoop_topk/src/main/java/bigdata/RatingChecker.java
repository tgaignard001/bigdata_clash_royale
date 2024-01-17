package bigdata;

public class RatingChecker implements IChecker{

    public RatingChecker(){
    }
    @Override
    public boolean checkDeck(DeckSummary deckSummary) {
        int MIN_UNIQUE_PLAYER = 50;
        int MIN_USES = 100;
        return deckSummary.uses > MIN_USES && deckSummary.uniquePlayers > MIN_UNIQUE_PLAYER;
    }

    @Override
    public double getValue(DeckSummary deckSummary) {
        if (deckSummary.uses == 0 || deckSummary.nbDiffForce == 0) return 0;
        double winRate = (double) deckSummary.victories / deckSummary.uses;
        double meanDiffForce = deckSummary.sumDiffForce / deckSummary.nbDiffForce;
        return winRate*meanDiffForce;
    }
}
