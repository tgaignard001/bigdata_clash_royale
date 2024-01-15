package bigdata;

import java.io.Serializable;
import java.time.Instant;

public class DeckSummary implements Cloneable, Serializable {
    public String sortedCards;
    public Instant date;
    public SummaryDateType dateType;
    public long victories;
    public long uses;
    public long uniquePlayers;
    public long highestClanLevel;
    public double sumDiffForce;
    public long nbDiffForce;

    DeckSummary() {}

    DeckSummary(String cards, Instant date, SummaryDateType dateType) {
        this.sortedCards = InputFields.sortCards(cards);
        this.date = date;
        this.dateType = dateType;
        this.victories = 0;
        this.uses = 0;
        this.uniquePlayers = 0;
        this.highestClanLevel = 0;
        this.sumDiffForce = 0;
        this.nbDiffForce = 0;
    }

    public double getMeanDiffForce() {
        return this.sumDiffForce / this.nbDiffForce;
    }

    public void incVictories() {
        this.victories++;
    }

    public void incUses() {
        this.uses++;
    }

    public void setHighestClanLevel(long highestClanLevel) {
        this.highestClanLevel = highestClanLevel;
    }

    public void addDiffForce(double diffForce) {
        this.sumDiffForce += diffForce;
    }

    public void incNbDiffForce() {
        this.nbDiffForce++;
    }

    public void updateDeckSummary(DeckSummary deckSummary) {
        this.victories += deckSummary.victories;
        this.uses += deckSummary.uses;
        this.highestClanLevel = Math.max(deckSummary.highestClanLevel, this.highestClanLevel);
        this.uniquePlayers = Math.max(deckSummary.uniquePlayers, this.uniquePlayers);
        this.sumDiffForce += deckSummary.sumDiffForce;
        this.nbDiffForce += deckSummary.nbDiffForce;
        this.date = (this.date == null) ? deckSummary.date : this.date;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sortedCards\": \"" + sortedCards + '\"' +
                ", \"date\": \"" + date + "\"" +
                ", \"dateType\": \"" + dateType + "\"" +
                ", \"victories\": " + victories +
                ", \"uses\": " + uses +
                ", \"uniquePlayers\": " + uniquePlayers +
                ", \"highestClanLevel\": " + highestClanLevel +
                ", \"MeanDiffForce\": " + getMeanDiffForce() +
                '}';
    }

    @Override
    public DeckSummary clone() {
        try {
            DeckSummary clone = (DeckSummary) super.clone();
            clone.sortedCards = this.sortedCards;
            clone.date = this.date;
            clone.dateType = this.dateType;
            clone.victories = this.victories;
            clone.uses = this.uses;
            clone.uniquePlayers = this.uniquePlayers;
            clone.highestClanLevel = this.highestClanLevel;
            clone.sumDiffForce = this.sumDiffForce;
            clone.nbDiffForce = this.nbDiffForce;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

    }
}
