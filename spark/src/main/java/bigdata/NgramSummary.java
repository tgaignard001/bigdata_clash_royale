package bigdata;

import java.io.Serializable;
import java.time.Instant;

public class NgramSummary extends DeckSummary implements Serializable, Cloneable  {
    NgramSummary(String cards, Instant date, SummaryDateType dateType){
        super(cards, date, dateType);
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
        return super.toString() +"\n";
    }
}
