import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.io.IOException;
import java.time.Instant;



public class ClashRoyaleCleaning {

    public static class ClashRoyaleCleaningMapper
            extends Mapper<Object, Text, Text, GameWritable> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            JSONObject game;
            try {
                game = new JSONObject(value.toString());
                long clanTr1 = game.has(InputFields.CLAN_TR1) ? game.getLong(InputFields.CLAN_TR1) : 0;
                long clanTr2 = game.has(InputFields.CLAN_TR2) ? game.getLong(InputFields.CLAN_TR2) : 0;
                if (InputFields.checkFields(game)) {
                    PlayerInfoWritable player1 = new PlayerInfoWritable(
                            game.getString(InputFields.PLAYER1),
                            game.getDouble(InputFields.ALL_DECK1),
                            game.getDouble(InputFields.DECK1),
                            InputFields.getCardsChecked(game.getString(InputFields.CARDS1)),
                            clanTr1,
                            game.getString(InputFields.CLAN1)
                    );

                    PlayerInfoWritable player2 = new PlayerInfoWritable(
                            game.getString(InputFields.PLAYER2),
                            game.getDouble(InputFields.ALL_DECK2),
                            game.getDouble(InputFields.DECK2),
                            InputFields.getCardsChecked(game.getString(InputFields.CARDS2)),
                            clanTr2,
                            game.getString(InputFields.CLAN2)
                    );

                    GameWritable gameWritable = new GameWritable(
                            Instant.parse(game.getString(InputFields.DATE)),
                            game.getLong(InputFields.ROUND),
                            game.getLong(InputFields.WIN),
                            player1,
                            player2
                    );
                    context.write(new Text(gameWritable.getId()), gameWritable);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ClashRoyaleCleaningReducer
            extends Reducer<Text, GameWritable, Text, GameWritable> {
        public void reduce(Text key, Iterable<GameWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, values.iterator().next().clone());
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
        job.setCombinerClass(ClashRoyaleCleaningReducer.class);
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
