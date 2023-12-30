import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeckSummaryWritable extends DeckSummary implements Writable {

    DeckSummaryWritable() {
        super();
    }

    DeckSummaryWritable(String cards, long year, long month, SummaryDateType dateType) {
        super(cards, year, month, dateType);
    }

    DeckSummaryWritable(DeckSummary deck){
        super(deck.getSortedCards(), deck.getYear(), deck.getMonth(), deck.getDateType());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(sortedCards);
        out.writeLong(year);
        out.writeLong(month);
        out.writeInt(dateType);
        out.writeLong(victories);
        out.writeLong(uses);
        out.writeLong(uniquePlayers);
        out.writeLong(highestClanLevel);
        out.writeDouble(sumDiffForce);
        out.writeLong(nbDiffForce);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        sortedCards = in.readUTF();
        year = in.readLong();
        month = in.readLong();
        dateType = in.readInt();
        victories = in.readLong();
        uses = in.readLong();
        uniquePlayers = in.readLong();
        highestClanLevel = in.readLong();
        sumDiffForce = in.readDouble();
        nbDiffForce = in.readLong();
    }

    @Override
    public DeckSummaryWritable clone() {
        DeckSummaryWritable clone = (DeckSummaryWritable) super.clone();
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

    }
}
