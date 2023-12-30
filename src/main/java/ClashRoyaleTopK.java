import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.*;

public class ClashRoyaleTopK {

    public static class ClashRoyaleTopKMapper
            extends Mapper<Text, DeckSummaryWritable, NullWritable, DeckSummaryWritable> {

        private final TreeMap<Double, DeckSummaryWritable> treeMap = new TreeMap<>();

        @Override
        protected void map(Text key, DeckSummaryWritable value, Context context) {
            TreeMapManager.deckAdder.addDeck(treeMap, value);
            TreeMapManager.maintainTreeMapSize(treeMap);
        }

        @Override
        protected void cleanup(Mapper<Text, DeckSummaryWritable, NullWritable, DeckSummaryWritable>.Context context)
                throws IOException, InterruptedException {
            TreeMapManager.sendSummaryFromTreeMaps(context, treeMap);
        }
    }

    public static class ClashRoyaleTopKReducer
            extends Reducer<NullWritable, DeckSummaryWritable, NullWritable, DeckSummaryWritable> {

        @Override
        public void reduce(NullWritable key, Iterable<DeckSummaryWritable> values, Context context)
                throws IOException, InterruptedException {
            TreeMap<Double, DeckSummaryWritable> treeMap = new TreeMap<>();

            for (DeckSummaryWritable value : values) {
                TreeMapManager.deckAdder.addDeck(treeMap, value);
                TreeMapManager.maintainTreeMapSize(treeMap);
            }
            TreeMapManager.sendSummaryFromTreeMaps(context, treeMap);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleTopK");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleTopK.class);
        job.setMapperClass(ClashRoyaleTopKMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(DeckSummaryWritable.class);
        job.setReducerClass(ClashRoyaleTopKReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(DeckSummaryWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
