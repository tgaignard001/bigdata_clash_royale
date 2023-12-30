import java.io.Serializable;
import java.time.Instant;

public class GameSpark extends Game implements Serializable {
    protected PlayerInfoSpark player1;
    protected PlayerInfoSpark player2;
    GameSpark() {
    }
    GameSpark(Game game){
        super(game);
        this.player1 = new PlayerInfoSpark(game.getPlayer1());
        this.player2 = new PlayerInfoSpark(game.getPlayer2());
    }

    public PlayerInfoSpark getPlayer1() {
        return player1;
    }
    public PlayerInfoSpark getPlayer2() {
        return player2;
    }

    @Override
    public GameSpark clone() {
        return (GameSpark) super.clone();
    }
}
