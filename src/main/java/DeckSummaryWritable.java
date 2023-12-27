import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeckSummaryWritable implements Writable, Cloneable {
    private long victories;
    private long uses;
    private long uniquePlayers;
    private long highestClanLevel;
    private double sumDiffForce;
    private long nbDiffForce;
    DeckSummaryWritable() {
        this.victories = 0;
        this.uses = 0;
        this.uniquePlayers = 0;
        this.highestClanLevel = 0;
        this.sumDiffForce = 0;
        this.nbDiffForce = 0;
    }

    public double getMeanDiffForce(){
        return this.sumDiffForce/this.nbDiffForce;
    }

    public double getVictories() {
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

    public void addDiffForce(double diffForce){
        this.sumDiffForce += diffForce;
    }

    public void incNbDiffForce(){
        this.nbDiffForce++;
    }

    public void updateDeckSummary(DeckSummaryWritable deckSummary){
        this.victories += deckSummary.victories;
        this.uses += deckSummary.uses;
        this.highestClanLevel = Math.max(deckSummary.highestClanLevel, this.highestClanLevel);
        this.uniquePlayers = Math.max(deckSummary.uniquePlayers, this.uniquePlayers);
        this.sumDiffForce += deckSummary.sumDiffForce;
        this.nbDiffForce += deckSummary.nbDiffForce;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(victories);
        out.writeLong(uses);
        out.writeLong(uniquePlayers);
        out.writeLong(highestClanLevel);
        out.writeDouble(sumDiffForce);
        out.writeLong(nbDiffForce);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        victories = in.readLong();
        uses = in.readLong();
        uniquePlayers = in.readLong();
        highestClanLevel = in.readLong();
        sumDiffForce = in.readDouble();
        nbDiffForce = in.readLong();
    }

    @Override
    public String toString() {
        return "DeckSummary{" +
                "victories=" + victories +
                ", uses=" + uses +
                ", uniquePlayers=" + uniquePlayers +
                ", highestClanLevel=" + highestClanLevel +
                ", sumDiffForce=" + sumDiffForce +
                ", nbDiffForce=" + nbDiffForce +
                '}';
    }

    @Override
    public DeckSummaryWritable clone() {
        try {
            DeckSummaryWritable clone = (DeckSummaryWritable) super.clone();
            clone.victories = this.victories;
            clone.uses = this.uses;
            clone.uniquePlayers = this.uniquePlayers;
            clone.highestClanLevel = this.highestClanLevel;
            clone.sumDiffForce = this.sumDiffForce;
            clone.nbDiffForce = this.nbDiffForce;
            return clone;
        }catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

    }
}
