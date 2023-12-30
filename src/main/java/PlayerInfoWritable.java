import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class PlayerInfoWritable implements Writable, Cloneable {
    private String player;
    private double allDeck;
    private double deck;
    private String cards;
    private long clanTr;
    private String clan;

PlayerInfoWritable() {
    }

public class PlayerInfoWritable extends PlayerInfo implements Writable {

    PlayerInfoWritable() {
        super();
    }

    PlayerInfoWritable(String player, double allDeck, double deck, String cards, long clanTr, String clan) {
        super(player, allDeck, deck, cards, clanTr, clan);
    }

    PlayerInfoWritable(PlayerInfo playerInfo) {
        super(playerInfo.getPlayer(), playerInfo.getAllDeck(), playerInfo.getDeck(), playerInfo.getCards(), playerInfo.getClanTr(), playerInfo.getClan());
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
        PlayerInfoWritable clone = (PlayerInfoWritable) super.clone();
        clone.player = this.player;
        clone.allDeck = this.allDeck;
        clone.deck = this.deck;
        clone.cards = this.cards;
        clone.clanTr = this.clanTr;
        clone.clan = this.clan;
        return clone;
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