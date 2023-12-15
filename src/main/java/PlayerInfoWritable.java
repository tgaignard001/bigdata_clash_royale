import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PlayerInfoWritable implements Writable, Cloneable {
    private String player;
    private double allDeck;
    private double deck;
    private String cards;
    private int clanTr;
    private String clan;

    PlayerInfoWritable() {

    }


    PlayerInfoWritable(String player, double allDeck, double deck, String cards, int clanTr, String clan) {
        this.player = player;
        this.allDeck = allDeck;
        this.deck = deck;
        this.cards = cards;
        this.clanTr = clanTr;
        this.clan = clan;
    }

    public String getPlayer() {
        return player;
    }

    public String getCards() {
        return cards;
    }

    public int getClanTr() {
        return clanTr;
    }

    public double getDeck() {
        return deck;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(player);
        out.writeDouble(allDeck);
        out.writeDouble(deck);
        out.writeUTF(cards);
        out.writeInt(clanTr);
        out.writeUTF(clan);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        player = in.readUTF();
        allDeck = in.readDouble();
        deck = in.readDouble();
        cards = in.readUTF();
        clanTr = in.readInt();
        clan = in.readUTF();
    }

    @Override
    public String toString() {
        return "Player: " + player +
                ", allDeck: " + allDeck +
                ", deck : " + deck +
                ", cards: " + cards +
                ", clanTr: " + clanTr +
                ", clan: " + clan;
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