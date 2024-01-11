package bigdata;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PlayerInfoWritable implements Writable, Cloneable {
    public String player;
    public double allDeck;
    public double deck;
    public String cards;
    public long clanTr;
    public String clan;

    public PlayerInfoWritable(){}
    public PlayerInfoWritable(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
        this.player = player;
        this.allDeck = allDeck;
        this.deck = deck;
        this.cards = cards;
        this.clanTr = clanTr;
        this.clan = clan;
    }
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(player);
        out.writeDouble(allDeck);
        out.writeDouble(deck);
        out.writeUTF(cards);
        out.writeLong(clanTr);
        out.writeUTF(clan);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        player = in.readUTF();
        allDeck = in.readDouble();
        deck = in.readDouble();
        cards = in.readUTF();
        clanTr = in.readLong();
        clan = in.readUTF();
    }

    @Override
    public String toString() {
        return "PlayerInfoWritable{" +
                "player='" + player + '\'' +
                ", allDeck=" + allDeck +
                ", deck=" + deck +
                ", cards='" + cards + '\'' +
                ", clanTr=" + clanTr +
                ", clan='" + clan + '\'' +
                '}';
    }

    @Override
    public PlayerInfoWritable clone() {
        try {
            PlayerInfoWritable clone = (PlayerInfoWritable) super.clone();
            clone.player = this.player;
            clone.allDeck = this.allDeck;
            clone.deck = this.deck;
            clone.cards = this.cards;
            clone.clanTr = this.clanTr;
            clone.clan = this.clan;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
