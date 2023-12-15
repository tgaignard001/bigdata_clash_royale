import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ClashRoyaleUniquePlayer {
    // Idée: on fait un autre job pour calculer le nombre de joueurs unique et on fera ensuite un join
    // On envoie <deckId, player>
    // dans chaque mapper, on crée un set local de tous les players reçus
    // on envoie tous les players du set vers le reducer
    // le reducer recrée le set en local et écrit la taille du set

    public static class ClashRoyaleCleaningMapper
            extends Mapper<Text, GameWritable, Text, DeckSummaryWritable> {

        public String sortCards(String cards){

            ArrayList<String> cardList = new ArrayList<>();
            for (int i = 0; i < cards.length() / 2; ++i) {
                String card = cards.substring(i * 2, i * 2 + 2);
                cardList.add(card);
            }

            Collections.sort(cardList);
            return String.join("", cardList);
        }

        @Override
        protected void map(Text key, GameWritable value, Context context) throws IOException, InterruptedException {

        }
    }

    public static class ClashRoyaleCleaningReducer
            extends Reducer<Text, DeckSummaryWritable, Text, DeckSummaryWritable> {
        public void reduce(Text key, Iterable<DeckSummaryWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, values.iterator().next());
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleSummary");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleUniquePlayer.class);
        job.setMapperClass(ClashRoyaleCleaningMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DeckSummaryWritable.class);
        job.setReducerClass(ClashRoyaleCleaningReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DeckSummaryWritable.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
