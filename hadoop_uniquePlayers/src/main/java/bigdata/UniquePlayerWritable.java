package bigdata;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

public class UniquePlayerWritable implements Writable, Cloneable {
    public String playerName;
    public String cards;
    public SummaryDateType dateType;
    public Instant date;

    public UniquePlayerWritable() {}

    public UniquePlayerWritable(String playerName, String cards, SummaryDateType dateType, Instant date) {
        this.playerName = playerName;
        this.cards = cards;
        this.dateType = dateType;
        this.date = date;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(playerName);
        out.writeUTF(cards);
        out.writeInt(dateType.ordinal());
        out.writeUTF(date.toString());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.playerName = in.readUTF();
        this.cards = in.readUTF();
        this.dateType = SummaryDateType.getDateType(in.readInt());
        this.date = Instant.parse(in.readUTF());
    }

    @Override
    public String toString() {
        return "UniquePlayerWritable{" +
                "playerName='" + playerName + '\'' +
                ", cards='" + cards + '\'' +
                ", dateType=" + dateType +
                ", date=" + date +
                '}';
    }

    @Override
    public UniquePlayerWritable clone() {
        try {
            UniquePlayerWritable clone = (UniquePlayerWritable) super.clone();
            clone.playerName = this.playerName;
            clone.cards = this.cards;
            clone.dateType = this.dateType;
            clone.date = this.date;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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
            return DateManager.isSameDate(this.date, player2.date, this.dateType);
        }
        return false;
    }


}
