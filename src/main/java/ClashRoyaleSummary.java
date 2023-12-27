import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class ClashRoyaleSummary {


    public static class ClashRoyaleSummaryMapper
            extends Mapper<Text, GameWritable, Text, DeckSummaryWritable> {

        @Override
        protected void map(Text key, GameWritable value, Context context) throws IOException, InterruptedException {

            PlayerInfoWritable player1 = value.getPlayer1();
            PlayerInfoWritable player2 = value.getPlayer2();

            String newKey1 = InputFields.sortCards(player1.getCards());
            String newKey2 = InputFields.sortCards(player2.getCards());
            DeckSummaryWritable deckSummary1 = new DeckSummaryWritable();
            DeckSummaryWritable deckSummary2 = new DeckSummaryWritable();

            if (value.getWin() == 1){
                deckSummary1.incVictories();
            }else {
                deckSummary2.incVictories();
            }

            deckSummary1.incUses();
            deckSummary2.incUses();

            deckSummary1.setHighestClanLevel(player1.getClanTr());
            deckSummary2.setHighestClanLevel(player2.getClanTr());

            double diffForce = player1.getDeck() - player2.getDeck();
            deckSummary1.addDiffForce(diffForce);
            deckSummary2.addDiffForce(-diffForce);

            deckSummary1.incNbDiffForce();
            deckSummary2.incNbDiffForce();

            context.write(new Text(newKey1), deckSummary1);
            context.write(new Text(newKey2), deckSummary2);
        }
    }



    public static class ClashRoyaleSummaryReducer
            extends Reducer<Text, DeckSummaryWritable, Text, DeckSummaryWritable> {
        public void reduce(Text key, Iterable<DeckSummaryWritable> values, Context context)
                throws IOException, InterruptedException {
            DeckSummaryWritable deckReduced = values.iterator().next().clone();
            while (values.iterator().hasNext()){
                deckReduced.updateDeckSummary(values.iterator().next());
            }
            context.write(key, deckReduced);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleSummary");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleSummary.class);
        job.setMapperClass(ClashRoyaleSummaryMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DeckSummaryWritable.class);
        job.setCombinerClass(ClashRoyaleSummaryReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DeckSummaryWritable.class);
        job.setReducerClass(ClashRoyaleSummaryReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DeckSummaryWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
