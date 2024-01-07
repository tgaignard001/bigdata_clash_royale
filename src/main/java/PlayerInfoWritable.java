import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class PlayerInfoWritable implements Cloneable, Serializable, Writable {
    private String player;
    private double allDeck;
    private double deck;
    private String cards;
    protected long clanTr;
    protected String clan;

    PlayerInfoWritable() {
    }

    PlayerInfoWritable(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
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

    public long getClanTr() {
        return clanTr;
    }

    public double getDeck() {
        return deck;
    }

    public double getAllDeck() {
        return allDeck;
    }

    public String getClan() {
        return clan;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerInfoWritable) {
            PlayerInfoWritable player2 = (PlayerInfoWritable) obj;
            String cards1 = InputFields.sortCards(this.cards);
            String cards2 = InputFields.sortCards(player2.getCards());
            return (this.player.equals(player2.getPlayer()) && cards1.equals(cards2));
        }
        return false;
    }
}