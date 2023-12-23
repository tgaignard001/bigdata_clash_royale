import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SummaryTopK implements Writable, Cloneable {
    private DeckTopK winRate = new DeckTopK();

    public DeckTopK getWinRate() {
        return winRate;
    }

    public void setWinRate(DeckTopK winRate) {
        this.winRate = winRate;
    }

    @Override
    public void write(DataOutput out) throws IOException{
        winRate.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException{
        winRate.readFields(in);
    }

    @Override
    public String toString() {
        return "winRate: " + winRate;
    }

    @Override
    public SummaryTopK clone() {
        try {
            SummaryTopK clone = (SummaryTopK) super.clone();
            clone.winRate = this.winRate.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
