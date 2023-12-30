import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UniquePlayerWritable extends UniquePlayer implements Writable {

    public UniquePlayerWritable() {
        super();
    }

    public UniquePlayerWritable(String playerName, String cards, long year, long month, SummaryDateType dateType) {
        this.playerName = playerName;
        this.cards = cards;
        this.year = year;
        this.month = month;
        this.dateType = dateType.ordinal();
    }

    public UniquePlayerWritable(UniquePlayer uniquePlayer){
        super(uniquePlayer.playerName, uniquePlayer.cards, uniquePlayer.getYear(), uniquePlayer.getMonth(), uniquePlayer.getDateType());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(playerName);
        out.writeUTF(cards);
        out.writeLong(year);
        out.writeLong(month);
        out.writeInt(dateType);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        playerName = in.readUTF();
        cards = in.readUTF();
        year = in.readLong();
        month = in.readLong();
        dateType = in.readInt();
    }

    @Override
    public UniquePlayerWritable clone() {
        UniquePlayerWritable clone = (UniquePlayerWritable) super.clone();
        clone.playerName = this.playerName;
        clone.cards = this.cards;
        clone.year = this.year;
        clone.month = this.month;
        clone.dateType = this.dateType;
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UniquePlayerWritable) {
            UniquePlayerWritable player2 = (UniquePlayerWritable) obj;
            String cards1 = InputFields.sortCards(this.cards);
            String cards2 = InputFields.sortCards(player2.cards);
            if (!this.playerName.equals(player2.playerName)) return false;
            if (!cards1.equals(cards2)) return false;
            if (this.dateType != player2.dateType) return false;
            SummaryDateType summaryDateType = SummaryDateType.getDateType(this.dateType);
            switch (summaryDateType) {
                case NONE:
                    return true;
                case YEARLY:
                    return this.year == player2.year;
                case MONTHLY:
                    return this.year == player2.year && this.month == player2.month;
            }
        }
        return false;
    }
}
