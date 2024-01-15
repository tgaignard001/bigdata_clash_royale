package bigdata;

import java.io.Serializable;

public class NgramSummary extends DeckSummary implements Serializable, Cloneable  {
    NgramSummary(){
        super();
    };
    public void updateSummary(NgramSummary ngramSummary){
        super.updateDeckSummary(ngramSummary);
    }
    public void updateFromDeckSummary(DeckSummary deckSummary){
        super.updateDeckSummary(deckSummary);
    }

    @Override
    public NgramSummary clone() {
        return (NgramSummary) super.clone();
    }

    @Override
    public String toString() {
        return super.toString() +"\n" + test;
    }
}
