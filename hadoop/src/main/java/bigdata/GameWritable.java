package bigdata;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

public class GameWritable implements Cloneable, Writable {

    public String id;
    public Instant date;
    public long round;
    public long win;
    public PlayerInfoWritable player1 = new PlayerInfoWritable();
    public PlayerInfoWritable player2 = new PlayerInfoWritable();

    public GameWritable(){
    }
    public GameWritable(Instant date, long round, long win, PlayerInfoWritable player1, PlayerInfoWritable player2) {
        this.date = date;
        this.round = round;
        this.win = win;
        this.player1 = player1;
        this.player2 = player2;
        String prefix = date.toString()+"-"+round;
        String suffix = player1.player.compareTo(player2.player) < 0 ? player1.player + player2.player : player2.player + player1.player;
        this.id = prefix + suffix;
    }
    public String getId(){
        return id;
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
        return "GameWritable{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", round=" + round +
                ", win=" + win +
                ", player1=" + player1 +
                ", player2=" + player2 +
                '}';
    }

    @Override
    public GameWritable clone() {
        try {
            GameWritable clone = (GameWritable) super.clone();
            clone.id = this.id;
            clone.date = this.date;
            clone.round = this.round;
            clone.win = this.win;
            clone.player1 = player1.clone();
            clone.player2 = player2.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
