package bigdata;

public class PlayerInfo implements Cloneable {
    public String player;
    public double allDeck;
    public double deck;
    public String cards;
    public long clanTr;
    public String clan;

    public PlayerInfo(){
        player = "null";
        allDeck = -1;
        deck = -1;
        cards = "0102030405060708";
        clanTr = -1;
        clan = "null";
    }

    public PlayerInfo(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
        this.player = player;
        this.allDeck = allDeck;
        this.deck = deck;
        this.cards = cards;
        this.clanTr = clanTr;
        this.clan = clan;
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
            String cards2 = InputFields.sortCards(player2.cards);
            return (this.player.equals(player2.player) && cards1.equals(cards2));
        }
        return false;
    }
}
