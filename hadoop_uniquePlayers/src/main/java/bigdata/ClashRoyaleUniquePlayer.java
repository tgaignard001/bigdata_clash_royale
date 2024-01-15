package bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClashRoyaleUniquePlayer {
    public static class ClashRoyaleUniquePlayerMapper
            extends Mapper<Text, GameWritable, Text, UniquePlayerWritable> {

        private final Set<UniquePlayerWritable> playerList = new HashSet<>();

        @Override
        protected void map(Text key, GameWritable value, Context context) {
            PlayerInfoWritable player1 = value.player1;
            PlayerInfoWritable player2 = value.player2;
            ArrayList<UniquePlayerWritable> uniquePlayerList = new ArrayList<>();
            for (SummaryDateType dateType : SummaryDateType.values()) {
                UniquePlayerWritable uniquePlayer1 = new UniquePlayerWritable(player1.player, player1.cards, dateType, value.date);
                UniquePlayerWritable uniquePlayer2 = new UniquePlayerWritable(player2.player, player2.cards, dateType, value.date);

                uniquePlayerList.add(uniquePlayer1);
                uniquePlayerList.add(uniquePlayer2);
            }
            playerList.addAll(uniquePlayerList);
        }

        @Override
        protected void cleanup(Mapper<Text, GameWritable, Text, UniquePlayerWritable>.Context context) throws IOException, InterruptedException {
            for (UniquePlayerWritable uniquePlayer : playerList) {
                String uniquePlayerKey = KeyManager.generateKey(uniquePlayer.cards, uniquePlayer.dateType, uniquePlayer.date);
                context.write(new Text(uniquePlayerKey), uniquePlayer);
            }
        }
    }

    public static class ClashRoyaleUniquePlayerReducer
            extends Reducer<Text, UniquePlayerWritable, Text, LongWritable> {


        public void reduce(Text key, Iterable<UniquePlayerWritable> values, Context context) throws IOException, InterruptedException {
            HashSet<String> playerList = new HashSet<>();
            while (values.iterator().hasNext()) {
                playerList.add(values.iterator().next().playerName);
            }
            context.write(key, new LongWritable(playerList.size()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleUniquePlayer");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleUniquePlayer.class);
        job.setMapperClass(ClashRoyaleUniquePlayerMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(UniquePlayerWritable.class);

        job.setReducerClass(ClashRoyaleUniquePlayerReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
