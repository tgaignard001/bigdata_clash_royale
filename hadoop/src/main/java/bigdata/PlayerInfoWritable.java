package bigdata;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PlayerInfoWritable implements Writable, Cloneable {

    PlayerInfo playerInfo;

    public PlayerInfoWritable(){
        playerInfo = new PlayerInfo();
    }
    public PlayerInfoWritable(PlayerInfo playerInfo){
        this.playerInfo = playerInfo;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(playerInfo.player);
        out.writeDouble(playerInfo.allDeck);
        out.writeDouble(playerInfo.deck);
        out.writeUTF(playerInfo.cards);
        out.writeLong(playerInfo.clanTr);
        out.writeUTF(playerInfo.clan);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        playerInfo.player = in.readUTF();
        playerInfo.allDeck = in.readDouble();
        playerInfo.deck = in.readDouble();
        playerInfo.cards = in.readUTF();
        playerInfo.clanTr = in.readLong();
        playerInfo.clan = in.readUTF();
    }

    @Override
    public String toString() {
        return playerInfo.toString();
    }

    @Override
    public PlayerInfoWritable clone() {
        try {
            PlayerInfoWritable clone = (PlayerInfoWritable) super.clone();
            clone.playerInfo = playerInfo.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
