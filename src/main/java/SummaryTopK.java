import org.apache.hadoop.io.Writable;

import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SummaryTopK implements Writable, Cloneable {
    private DeckTopK winRate = new DeckTopK();
    private DeckTopK meanDiffForce = new DeckTopK();

    public DeckTopK getWinRate() {
        return winRate;
    }

    public DeckTopK getMeanDiffForce() {
        return meanDiffForce;
    }

    public void setValues(DeckTopK winRate, DeckTopK meanDiffForce) {
        this.winRate = winRate;
        this.meanDiffForce = meanDiffForce;
    }

    public void setMeanDiffForce(DeckTopK meanDiffForce) {
        this.meanDiffForce = meanDiffForce;
    }

    @Override
    public void write(DataOutput out) throws IOException{
        winRate.write(out);
        meanDiffForce.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException{
        winRate.readFields(in);
        meanDiffForce.readFields(in);
    }

    @Override
    public String toString() {
        return "winRate: " + winRate
                + "meanDiffForce: " + meanDiffForce;
    }

    @Override
    public SummaryTopK clone() {
        try {
            SummaryTopK clone = (SummaryTopK) super.clone();
            clone.winRate = this.winRate.clone();
            clone.meanDiffForce = this.meanDiffForce.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
