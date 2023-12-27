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
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClashRoyaleUniquePlayer {
    // Idée: on fait un autre job pour calculer le nombre de joueurs unique et on fera ensuite un join
    // On envoie <deckId, player>
    // dans chaque mapper, on crée un set local de tous les players reçus
    // on envoie tous les players du set vers le reducer
    // le reducer recrée le set en local et écrit la taille du set

    public static class ClashRoyaleUniquePlayerMapper
            extends Mapper<Text, GameWritable, Text, PlayerInfoWritable> {

        private final Set<PlayerInfoWritable> playerList = new HashSet<>();
        @Override
        protected void map(Text key, GameWritable value, Context context) {
            PlayerInfoWritable player1 = value.getPlayer1();
            PlayerInfoWritable player2 = value.getPlayer2();
            playerList.add(player1.clone());
            playerList.add(player2.clone());
        }

        @Override
        protected void cleanup(Mapper<Text, GameWritable, Text, PlayerInfoWritable>.Context context) throws IOException, InterruptedException {
            for (PlayerInfoWritable player : playerList) {
                context.write(new Text(InputFields.sortCards(player.getCards())), player);
            }
        }
    }

    public static class ClashRoyaleUniquePlayerReducer
            extends Reducer<Text, PlayerInfoWritable, Text, LongWritable> {


        public void reduce(Text key, Iterable<PlayerInfoWritable> values, Context context) throws IOException, InterruptedException {
            HashSet<PlayerInfoWritable> playerList = new HashSet<>();
            while (values.iterator().hasNext()) {
                playerList.add(values.iterator().next().clone());
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
        job.setMapOutputValueClass(PlayerInfoWritable.class);
        job.setReducerClass(ClashRoyaleUniquePlayerReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
