import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

public class GameWritable implements Writable, Cloneable {
    private String id;
    private Instant date;
    private long round;
    private long win;
    private PlayerInfoWritable player1 = new PlayerInfoWritable();
    private PlayerInfoWritable player2 = new PlayerInfoWritable();

    GameWritable() {
    }

    GameWritable(Instant date, long round, long win, PlayerInfoWritable player1, PlayerInfoWritable player2) {
        this.date = date;
        this.round = round;
        this.win = win;
        this.player1 = player1;
        this.player2 = player2;
        String prefix = date.toString() + "-" + round;
        String suffix = player1.getPlayer().compareTo(player2.getPlayer()) < 0 ? player1.getPlayer() + player2.getPlayer() : player2.getPlayer() + player1.getPlayer();
        this.id = prefix + "-" + suffix;
    }

    public String getId() {
        return id;
    }

    public PlayerInfoWritable getPlayer1() {
        return player1;
    }

    public PlayerInfoWritable getPlayer2() {
        return player2;
    }

    public long getWin() {
        return win;
    }

    public Instant getDate() {
        return date;
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
        return "id: " + id +
                ", date: " + date +
                ", round: " + round +
                ", win: " + win +
                ", player1: " + player1.getPlayer() +
                ", player2: " + player2.getPlayer();
    }

    @Override
    public GameWritable clone() {
        try {
            GameWritable clone = (GameWritable) super.clone();
            clone.id = this.id;
            clone.date = this.date;
            clone.round = this.round;
            clone.win = this.win;
            clone.player1 = this.player1;
            clone.player2 = this.player2;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}