import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.*;

public class ClashRoyaleTopK {

    public static class ClashRoyaleTopKMapper
            extends Mapper<Text, DeckSummaryWritable, NullWritable, SummaryTopK> {

        private final TreeMap<Double, String> winRateTopK = new TreeMap<Double, String>();
        private final TreeMap<Double, String> meanDiffForceTopK = new TreeMap<Double, String>();

        private int k = 10;

        /**
         * Add in winRateTopk tree the winRate if the value is interesting
         * @param id String with the cards of the deck
         * @param deck DeckSummary with information of the deck
         */
        private void addWinRate(String id, DeckSummaryWritable deck){
            double victories = deck.getVictories();
            double uses = deck.getUses();
            if (uses > 100) {
                double winRate = victories/uses;
                winRateTopK.put(winRate, id);
            }
        }

        /**
         * Add in meanDiffForceTopk tree the mean difference force of the deck if the value is interesting
         * @param id String with the cards of the deck
         * @param deck DeckSummary with information of the deck
         */
        private void addMeanDiffForce(String id, DeckSummaryWritable deck){
            double meanDiffForce = deck.getMeanDiffForce();
            meanDiffForceTopK.put(meanDiffForce, id);
        }

        @Override
        protected void setup(Mapper<Text, DeckSummaryWritable, NullWritable, SummaryTopK>.Context context)
                throws IOException, InterruptedException {
            this.k = context.getConfiguration().getInt("k", 10);
        }

        @Override
        protected void map(Text key, DeckSummaryWritable value, Context context) throws IOException, InterruptedException {
            addWinRate(key.toString(), value);
            addMeanDiffForce(key.toString(), value);
            TreeMapManager.maintainTreeMapSize(winRateTopK, k);
            TreeMapManager.maintainTreeMapSize(meanDiffForceTopK, k);
        }

        @Override
        protected void cleanup(Mapper<Text, DeckSummaryWritable, NullWritable, SummaryTopK>.Context context)
                throws IOException, InterruptedException {
            TreeMapManager.sendSummaryFromTreeMaps(context, winRateTopK, meanDiffForceTopK);
        }
    }

    public static class ClashRoyaleTopKReducer
            extends Reducer<NullWritable, SummaryTopK, NullWritable, SummaryTopK> {
        private int k = 10;

        @Override
        protected void setup(Reducer<NullWritable, SummaryTopK, NullWritable, SummaryTopK>.Context context)
                throws IOException, InterruptedException {
            this.k = context.getConfiguration().getInt("k", 10);
        }

        public void reduce(Text key, Iterable<SummaryTopK> values, Context context)
                throws IOException, InterruptedException {
            TreeMap<Double, String> winRateTopK = new TreeMap<Double, String>();
            TreeMap<Double, String> meanDiffForceTopK = new TreeMap<Double, String>();

            for (SummaryTopK value : values){
                winRateTopK.put(value.getWinRate().getValue(), value.getWinRate().getId());
                meanDiffForceTopK.put(value.getMeanDiffForce().getValue(), value.getMeanDiffForce().getId());
                TreeMapManager.maintainTreeMapSize(winRateTopK, k);
                TreeMapManager.maintainTreeMapSize(meanDiffForceTopK, k);
            }

            TreeMapManager.sendSummaryFromTreeMaps(context, winRateTopK, meanDiffForceTopK);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleTopK");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleTopK.class);
        job.setMapperClass(ClashRoyaleTopKMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(SummaryTopK.class);
        job.setReducerClass(ClashRoyaleTopKReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(SummaryTopK.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
