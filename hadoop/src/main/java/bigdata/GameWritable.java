package bigdata;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

public class GameWritable implements Cloneable, Writable {

    private Game game;
    private PlayerInfoWritable player1;
    private PlayerInfoWritable player2;

    public GameWritable(){
        game = new Game();
        player1 = new PlayerInfoWritable();
        player2 = new PlayerInfoWritable();
    }

    public GameWritable(Game game){
        this.game = game;
        this.player1 = new PlayerInfoWritable(game.player1);
        this.player2 = new PlayerInfoWritable(game.player2);
    }

    public String getId(){
        return game.id;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(game.id);
        out.writeUTF(game.date.toString());
        out.writeLong(game.round);
        out.writeLong(game.win);
        player1.write(out);
        player2.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        game.id = in.readUTF();
        game.date = Instant.parse(in.readUTF());
        game.round = in.readLong();
        game.win = in.readLong();
        player1.readFields(in);
        player2.readFields(in);
    }

    @Override
    public String toString() {
        return game.toString();
    }

    @Override
    public GameWritable clone() {
        try {
            GameWritable clone = (GameWritable) super.clone();
            clone.game = game.clone();
            clone.player1 = player1.clone();
            clone.player2 = player2.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
