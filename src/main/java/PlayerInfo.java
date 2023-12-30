import java.io.Serializable;

public class PlayerInfo implements Cloneable, Serializable{
    protected String player;
    protected double allDeck;
    protected double deck;
    protected String cards;
    protected long clanTr;
    protected String clan;

    PlayerInfo() {}

    PlayerInfo(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
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
    public PlayerInfo clone() {
        try {
            PlayerInfo clone = (PlayerInfo) super.clone();
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
        if (obj instanceof PlayerInfo){
            PlayerInfo player2 = (PlayerInfo) obj;
            String cards1 = InputFields.sortCards(this.cards);
            String cards2 = InputFields.sortCards(player2.getCards());
            return (this.player.equals(player2.getPlayer()) && cards1.equals(cards2));
        }
        return false;
    }
}