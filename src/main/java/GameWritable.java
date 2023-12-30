import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;

public class GameWritable extends Game implements Writable {
    private PlayerInfoWritable player1 = new PlayerInfoWritable();
    private PlayerInfoWritable player2 = new PlayerInfoWritable();

    GameWritable(){
    }

    GameWritable(Instant date, long round, long win, PlayerInfoWritable player1, PlayerInfoWritable player2) {
        super(date, round, win, player1, player2);
    }

    GameWritable(Game game) {
        super(game.getDate(), game.getRound(), game.getWin(), game.getPlayer1(), game.getPlayer2());
        this.player1 = new PlayerInfoWritable(game.getPlayer1());
        this.player2 = new PlayerInfoWritable(game.getPlayer2());
    }

    public PlayerInfoWritable getPlayer1() {
        return player1;
    }
    public PlayerInfoWritable getPlayer2() {
        return player2;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(date.toString());
        out.writeLong(round);
        out.writeLong(win);
        player1.write(out);
        player2.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readUTF();
        date = Instant.parse(in.readUTF());
        round = in.readLong();
        win = in.readLong();
        player1.readFields(in);
        player2.readFields(in);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public GameWritable clone() {
        GameWritable clone = (GameWritable) super.clone();
        clone.id = this.id;
        clone.date = this.date;
        clone.round = this.round;
        clone.win = this.win;
        clone.player1 = this.player1;
        clone.player2 = this.player2;
        return clone;
    }
}