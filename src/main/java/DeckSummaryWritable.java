import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeckSummaryWritable implements Writable, Cloneable {
    private int victories;
    private int uses;
    private int uniquePlayers;
    private int highestClanLevel;
    private double sumDiffForce;
    private int nbDiffForce;
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

    public void incVictories() {
        this.victories++;
    }

    public void incUses() {
        this.uses++;
    }

    public void setHighestClanLevel(int highestClanLevel) {
        this.highestClanLevel = highestClanLevel;
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
        this.sumDiffForce += deckSummary.sumDiffForce;
        this.nbDiffForce += deckSummary.nbDiffForce;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(victories);
        out.writeInt(uses);
        out.writeInt(uniquePlayers);
        out.writeInt(highestClanLevel);
        out.writeDouble(sumDiffForce);
        out.writeInt(nbDiffForce);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        victories = in.readInt();
        uses = in.readInt();
        uniquePlayers = in.readInt();
        highestClanLevel = in.readInt();
        sumDiffForce = in.readDouble();
        nbDiffForce = in.readInt();
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
    protected Object clone() throws CloneNotSupportedException {
        DeckSummaryWritable clone = (DeckSummaryWritable) super.clone();
        clone.victories = this.victories;
        clone.uses = this.uses;
        clone.uniquePlayers = this.uniquePlayers;
        clone.highestClanLevel = this.highestClanLevel;
        clone.sumDiffForce = this.sumDiffForce;
        clone.nbDiffForce = this.nbDiffForce;
        return clone;
    }
}
