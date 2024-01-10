package bigdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.time.Instant;

public class ClashRoyaleCleaning {
    public static class ClashRoyaleCleaningMapper
            extends Mapper<Object, Text, Text, GameWritable> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode gameJson = objectMapper.readTree(value.toString());
            if (InputFields.checkFields(gameJson)) {
                long clanTr1 = gameJson.has(InputFields.CLAN_TR1) ? gameJson.get(InputFields.CLAN_TR1).asLong() : 0;
                long clanTr2 = gameJson.has(InputFields.CLAN_TR2) ? gameJson.get(InputFields.CLAN_TR2).asLong() : 0;
                GameWritable gameWritable = new GameWritable(new Game(
                        Instant.parse(gameJson.get(InputFields.DATE).asText()),
                        gameJson.get(InputFields.ROUND).asLong(),
                        gameJson.get(InputFields.WIN).asLong(),
                        new PlayerInfo(
                                gameJson.get(InputFields.PLAYER1).asText(),
                                gameJson.get(InputFields.ALL_DECK1).asDouble(),
                                gameJson.get(InputFields.DECK1).asDouble(),
                                InputFields.getCardsChecked(gameJson.get(InputFields.CARDS1).asText()),
                                clanTr1,
                                gameJson.get(InputFields.CLAN1).asText()
                        ),
                        new PlayerInfo(
                                gameJson.get(InputFields.PLAYER2).asText(),
                                gameJson.get(InputFields.ALL_DECK2).asDouble(),
                                gameJson.get(InputFields.DECK2).asDouble(),
                                InputFields.getCardsChecked(gameJson.get(InputFields.CARDS2).asText()),
                                clanTr2,
                                gameJson.get(InputFields.CLAN2).asText()
                        ))
                );
                context.write(new Text(gameWritable.getId()), gameWritable);
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
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
