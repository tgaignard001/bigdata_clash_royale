import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;


public class ClashRoyaleCleaning {

    public static class ClashRoyaleCleaningMapper
            extends Mapper<Object, Text, Text, GameWritable> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode gameJson = objectMapper.readTree(value.toString());
            if (InputFields.checkFields(gameJson)) {
                GameWritable gameWritable = new GameWritable(InputFields.createGame(gameJson));
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
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
