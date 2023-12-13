import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;
import java.util.*;


public class ClashRoyaleCleaning {

    public static class ClashRoyaleCleaningMapper
            extends Mapper<Object, Text, Text, GameWritable> {
        private static final String PLAYER1 = "player";
        private static final String ALL_DECK1 = "all_deck";
        private static final String DECK1 = "deck";
        private static final String CARDS1 = "cards";
        private static final String CLAN1 = "clan";
        private static final String CLAN_TR1 = "clanTr";
        private static final String TOUCH1 = "touch";
        private static final String PLAYER2 = "player";
        private static final String ALL_DECK2 = "all_deck";
        private static final String DECK2 = "deck";
        private static final String CARDS2 = "cards";
        private static final String CLAN2 = "clan";
        private static final String CLAN_TR2 = "clanTr";
        private static final String TOUCH2 = "touch2";
        private static final String DATE = "date";
        private static final String ROUND = "round";
        private static final String WIN = "win";


        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            JSONObject game = null;
            try {
                game = new JSONObject(value.toString());
                int clanTr1 = game.has(CLAN_TR1) ?  game.getInt(CLAN_TR1):0;
                int clanTr2 = game.has(CLAN_TR2) ?  game.getInt(CLAN_TR2):0;
                int touch1 = game.has(TOUCH1) ?  game.getInt(TOUCH1):0;
                int touch2 = game.has(TOUCH2) ?  game.getInt(TOUCH2):0;
                if (touch1 == 1 && touch2 == 1) {
                    if (game.has(PLAYER1) && game.has(PLAYER1)
                            && game.has(ALL_DECK1) && game.has(ALL_DECK2)
                            && game.has((DECK1)) && game.has(DECK2)
                            && game.has(CARDS1) && game.has(CARDS2)
                            && game.has(CLAN1) && game.has(CLAN2)
                            && game.has(DATE)
                            && game.has(ROUND)
                            && game.has(WIN)
                    ) {
                        PlayerInfoWritable player1 = new PlayerInfoWritable(
                                game.getString(PLAYER1),
                                game.getDouble(ALL_DECK1),
                                game.getDouble(DECK1),
                                game.getString(CARDS1),
                                clanTr1,
                                game.getString(CLAN1)
                        );

                        PlayerInfoWritable player2 = new PlayerInfoWritable(
                                game.getString(PLAYER2),
                                game.getDouble(ALL_DECK2),
                                game.getDouble(DECK2),
                                game.getString(CARDS2),
                                clanTr2,
                                game.getString(CLAN2)
                        );

                        GameWritable gameWritable = new GameWritable(
                                Instant.parse(game.getString(DATE)),
                                game.getInt(ROUND),
                                game.getInt(WIN),
                                player1,
                                player2
                        );
                        context.write(new Text(gameWritable.id), gameWritable);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ClashRoyaleCleaningReducer
            extends Reducer<Text, GameWritable, Text, GameWritable> {
        public void reduce(Text key, Iterable<GameWritable> values,Context context )
                throws IOException, InterruptedException {
            context.write(key, values.iterator().next());
        }
    }

    public static class GameWritable implements Writable, Cloneable {
        String id;
        Instant date;
        int round;
        int win;
        PlayerInfoWritable player1 = new PlayerInfoWritable();
        PlayerInfoWritable player2 = new PlayerInfoWritable();

        GameWritable() {

        }


        GameWritable(Instant date, int round, int win, PlayerInfoWritable player1, PlayerInfoWritable player2) {
            this.date = date;
            this.round = round;
            this.win = win;
            this.player1 = player1;
            this.player2 = player2;
            String prefix = date.toString()+"-"+round;
            String suffix = player1.player.compareTo(player2.player) < 0 ? player1.player + player2.player : player2.player + player1.player;
            this.id = prefix +"-"+ suffix;
        }



        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(id);
            out.writeUTF(date.toString());
            out.writeInt(round);
            out.writeInt(win);
            player1.write(out);
            player2.write(out);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            id = in.readUTF();
            date = Instant.parse(in.readUTF());
            round = in.readInt();
            win = in.readInt();
            player1.readFields(in);
            player2.readFields(in);
        }

      @Override
      public String toString() {
        return "id: " + id +
                ", date: " + date +
                ", round: " + round +
                ", win: " + win +
                ", player1: " + player1.player +
                ", player2: " + player2.player;
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

    public static class PlayerInfoWritable implements Writable, Cloneable {
        String player;
        double allDeck;
        double deck;
        String cards;
        int clanTr;
        String clan;

        PlayerInfoWritable() {

        }



        PlayerInfoWritable(String player, double allDeck, double deck, String cards, int clanTr, String clan) {
            this.player = player;
            this.allDeck = allDeck;
            this.deck = deck;
            this.cards = cards;
            this.clanTr = clanTr;
            this.clan = clan;
        }



        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(player);
            out.writeDouble(allDeck);
            out.writeDouble(deck);
            out.writeUTF(cards);
            out.writeInt(clanTr);
            out.writeUTF(clan);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            player = in.readUTF();
            allDeck = in.readDouble();
            deck = in.readDouble();
            cards = in.readUTF();
            clanTr = in.readInt();
            clan = in.readUTF();
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
        public PlayerInfoWritable clone() {
            try {
                PlayerInfoWritable clone = (PlayerInfoWritable) super.clone();
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
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleCleaning");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleCleaning.class);
        job.setMapperClass(ClashRoyaleCleaningMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(GameWritable.class);
        job.setReducerClass(ClashRoyaleCleaningReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(GameWritable.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
