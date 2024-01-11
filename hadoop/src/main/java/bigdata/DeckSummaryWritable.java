package bigdata;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

public class DeckSummaryWritable implements Writable, Cloneable {

    public DeckSummary deckSummary = new DeckSummary();

    DeckSummaryWritable() {}

    DeckSummaryWritable(DeckSummary deckSummary) {
        this.deckSummary = deckSummary;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(deckSummary.sortedCards);
        out.writeUTF(deckSummary.date.toString());
        out.writeInt(deckSummary.dateType.ordinal());
        out.writeLong(deckSummary.victories);
        out.writeLong(deckSummary.uses);
        out.writeLong(deckSummary.uniquePlayers);
        out.writeLong(deckSummary.highestClanLevel);
        out.writeDouble(deckSummary.sumDiffForce);
        out.writeLong(deckSummary.nbDiffForce);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        deckSummary.sortedCards = in.readUTF();
        deckSummary.date = Instant.parse(in.readUTF());
        deckSummary.dateType = SummaryDateType.getDateType(in.readInt());
        deckSummary.victories = in.readLong();
        deckSummary.uses = in.readLong();
        deckSummary.uniquePlayers = in.readLong();
        deckSummary.highestClanLevel = in.readLong();
        deckSummary.sumDiffForce = in.readDouble();
        deckSummary.nbDiffForce = in.readLong();
    }

    @Override
    public String toString() {
        return deckSummary.toString();
    }

    @Override
    public DeckSummaryWritable clone() {
        try {
            DeckSummaryWritable clone = (DeckSummaryWritable) super.clone();
            clone.deckSummary = this.deckSummary.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
