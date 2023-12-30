import java.io.Serializable;

public class PlayerInfoSpark extends  PlayerInfo implements Serializable {
    private static final long serialVersionUID = -2685444218382696366L;
    PlayerInfoSpark(PlayerInfo playerInfo){
        super(playerInfo.getPlayer(), playerInfo.getAllDeck(), playerInfo.getDeck(), playerInfo.getCards(), playerInfo.getClanTr(), playerInfo.getClan());
    }

    PlayerInfoSpark(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
        this.player = player;
        this.allDeck = allDeck;
        this.deck = deck;
        this.cards = cards;
        this.clanTr = clanTr;
        this.clan = clan;
    }

    @Override
    public PlayerInfoSpark clone() {
        return (PlayerInfoSpark) super.clone();
    }
}
