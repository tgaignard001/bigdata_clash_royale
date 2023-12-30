import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;

public class Game implements Cloneable, Serializable {
    protected String id;
    protected Instant date;
    protected long round;
    protected long win;
    protected PlayerInfo player1;
    protected PlayerInfo player2;

    Game(){

    }
    Game(Instant date, long round, long win, PlayerInfo player1, PlayerInfo player2) {
        this.date = date;
        this.round = round;
        this.win = win;
        this.player1 = player1;
        this.player2 = player2;
        String prefix = date.toString()+"-"+round;
        String suffix = player1.getPlayer().compareTo(player2.getPlayer()) < 0 ? player1.getPlayer() + player2.getPlayer() : player2.getPlayer() + player1.getPlayer();
        this.id = prefix + suffix;
    }

    Game(Game game){
        this(game.getDate(), game.getRound(), game.getWin(), game.getPlayer1(), game.getPlayer2());
    }

    public String getId() {
        return id;
    }
    public long getWin() {
        return win;
    }

    public long getRound() {
        return round;
    }

    public Instant getDate() {
        return date;
    }

    public PlayerInfo getPlayer1() {
        return player1;
    }
    public PlayerInfo getPlayer2() {
        return player2;
    }

    @Override
    public String toString() {
        return "id: " + id +
                ", date: " + date +
                ", round: " + round +
                ", win: " + win +
                ", player1: " + player1 +
                ", player2: " + player2;
    }

    @Override
    public Game clone() {
        try {
            Game clone = (Game) super.clone();
            clone.id = this.id;
            clone.date = this.date;
            clone.round = this.round;
            clone.win = this.win;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}