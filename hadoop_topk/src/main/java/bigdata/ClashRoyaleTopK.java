package bigdata;

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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ClashRoyaleTopK {

    public static class ClashRoyaleTopKMapper
            extends Mapper<Text, DeckSummaryWritable, NullWritable, DeckSummaryWritable> {
        TreeMapManager treeMapManager;

        @Override
        protected void map(Text key, DeckSummaryWritable value, Context context) {
            treeMapManager = new TreeMapManager();
            treeMapManager.addNewDeck(value.deckSummary);
        }

        @Override
        protected void cleanup(Mapper<Text, DeckSummaryWritable, NullWritable, DeckSummaryWritable>.Context context)
                throws IOException, InterruptedException {
            HashMap<String, TreeMap<Double, DeckSummary>> treeList = treeMapManager.getTreeList();
            context.write(NullWritable.get(), new DeckSummaryWritable(new DeckSummary("0000000000000000", Instant.now(), SummaryDateType.NONE)));
            for (TreeMap<Double, DeckSummary> tree : treeList.values()){
                for (Map.Entry<Double, DeckSummary> entry : tree.entrySet()) {
                    context.write(NullWritable.get(), new DeckSummaryWritable(entry.getValue()));
                }
            }
        }
    }

    public static class ClashRoyaleTopKReducer
            extends Reducer<NullWritable, DeckSummaryWritable, NullWritable, String> {

        @Override
        public void reduce(NullWritable key, Iterable<DeckSummaryWritable> values, Context context)
                throws IOException, InterruptedException {

            TreeMapManager treeMapManager = new TreeMapManager();
            int counter = 0;
            while (values.iterator().hasNext()){
                treeMapManager.addNewDeck(values.iterator().next().deckSummary.clone());
                counter++;
            }
            context.write(NullWritable.get(), "Nombre de données dans le reducer: " + counter);
            ArrayList<DeckSummary> line = treeMapManager.getTopKLine();
            while(line != null){
                context.write(NullWritable.get(), line.toString());
                line = treeMapManager.getTopKLine();
            }
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
