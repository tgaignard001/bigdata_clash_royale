import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class UniquePlayer implements Serializable, Cloneable {
    protected String playerName;
    protected String cards;
    protected long year;
    protected long month;
    protected int dateType;

    public UniquePlayer() {
    }

    public UniquePlayer(String playerName, String cards, long year, long month, SummaryDateType dateType) {
        this.playerName = playerName;
        this.cards = cards;
        this.year = year;
        this.month = month;
        this.dateType = dateType.ordinal();
    }

    public String getCards() {
        return cards;
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public SummaryDateType getDateType() {
        return SummaryDateType.getDateType(dateType);
    }



    @Override
    public String toString() {
        return "UniquePlayerWritable{" +
                "playerName='" + playerName + '\'' +
                ", cards='" + cards + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", dateType=" + dateType +
                '}';
    }

    @Override
    public UniquePlayer clone() {
        try {
            UniquePlayer clone = (UniquePlayer) super.clone();
            clone.playerName = this.playerName;
            clone.cards = this.cards;
            clone.year = this.year;
            clone.month = this.month;
            clone.dateType = this.dateType;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UniquePlayer) {
            UniquePlayer player2 = (UniquePlayer) obj;
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
