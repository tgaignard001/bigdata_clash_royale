import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class DeckSummary implements Serializable, Cloneable {
    protected String sortedCards;
    protected long year;
    protected long month;
    protected int dateType;
    protected long victories;
    protected long uses;
    protected long uniquePlayers;
    protected long highestClanLevel;
    protected double sumDiffForce;
    protected long nbDiffForce;

    DeckSummary() {
    }

    DeckSummary(String cards, long year, long month, SummaryDateType dateType) {
        this.sortedCards = InputFields.sortCards(cards);
        this.year = year;
        this.month = month;
        this.dateType = dateType.ordinal();
        this.victories = 0;
        this.uses = 0;
        this.uniquePlayers = 0;
        this.highestClanLevel = 0;
        this.sumDiffForce = 0;
        this.nbDiffForce = 0;
    }

    public String getSortedCards() {
        return sortedCards;
    }

    public SummaryDateType getDateType() {
        return SummaryDateType.getDateType(dateType);
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public double getMeanDiffForce() {
        return this.sumDiffForce / this.nbDiffForce;
    }

    public long getVictories() {
        return victories;
    }

    public long getUses() {
        return uses;
    }

    public long getUniquePlayers() {
        return uniquePlayers;
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

    public void setUniquePlayers(long uniquePlayers) {
        this.uniquePlayers = uniquePlayers;
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
        this.year = (this.year == 0) ? deckSummary.year : this.year;
        this.month = (this.month == 0) ? deckSummary.month : this.month;
    }

    @Override
    public String toString() {
        return "{" +
                "\"cards\": \"" + sortedCards + "\"" +
                ", \"year\": " + year +
                ", \"month\": " + month +
                ", \"dateType\": \"" + SummaryDateType.getDateType(dateType) + "\"" +
                ", \"victories\": " + victories +
                ", \"uses\": " + uses +
                ", \"uniquePlayers\": " + uniquePlayers +
                ", \"highestClanLevel\": " + highestClanLevel +
                ", \"sumDiffForce\": " + sumDiffForce +
                ", \"nbDiffForce\": " + nbDiffForce +
                '}';
    }

    @Override
    public DeckSummary clone() {
        try {
            DeckSummary clone = (DeckSummary) super.clone();
            clone.sortedCards = this.sortedCards;
            clone.year = this.year;
            clone.month = this.month;
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
