import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;

public class ClashRoyaleSummary {


    public static class ClashRoyaleSummaryMapper
            extends Mapper<Text, GameWritable, Text, DeckSummaryWritable> {

        @Override
        protected void map(Text key, GameWritable value, Context context) throws IOException, InterruptedException {

            SummaryCreator summaryCreator = new SummaryCreator(value.getPlayer1(), value.getPlayer2(), value.getDate(), value.getWin());

            DeckSummaryWritable deckSummaryWritable;

            for (DeckSummary deckSummary : summaryCreator.generateSummaries()) {
                deckSummaryWritable = new DeckSummaryWritable(deckSummary);
                String deckSummaryKey = SummaryCreator.generateKey(deckSummaryWritable.getSortedCards(), deckSummaryWritable.getDateType(), deckSummaryWritable.getYear(), deckSummaryWritable.getMonth());
                context.write(new Text(deckSummaryKey), deckSummaryWritable);
            }
        }
    }

    public static class ClashRoyaleUniqueMapper
            extends Mapper<Text, LongWritable, Text, DeckSummaryWritable> {
        @Override
        protected void map(Text key, LongWritable value, Context context) throws IOException, InterruptedException {
            DeckSummaryWritable deckSummary = new DeckSummaryWritable(SummaryCreator.generateSummaryFromKeyAndUniquePlayersCount(key.toString(), value.get()));
            context.write(key, deckSummary);
        }
    }

    public static class ClashRoyaleSummaryReducer
            extends Reducer<Text, DeckSummaryWritable, Text, DeckSummaryWritable> {
        public void reduce(Text key, Iterable<DeckSummaryWritable> values, Context context)
                throws IOException, InterruptedException {
            DeckSummaryWritable deckReduced = values.iterator().next().clone();
            while (values.iterator().hasNext()) {
                deckReduced.updateDeckSummary(values.iterator().next().clone());
            }
            context.write(key, deckReduced);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleSummary");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleSummary.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), SequenceFileInputFormat.class, ClashRoyaleSummaryMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), SequenceFileInputFormat.class, ClashRoyaleUniqueMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DeckSummaryWritable.class);

        job.setCombinerClass(ClashRoyaleSummaryReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DeckSummaryWritable.class);

        job.setReducerClass(ClashRoyaleSummaryReducer.class);

        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
