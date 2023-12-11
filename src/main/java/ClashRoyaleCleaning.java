import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class ClashRoyaleCleaning {
    public static IntWritable getCount(Iterable<IntWritable> values){
        int count = 0;
        for (IntWritable i: values) {
            count += i.get();
        }
        return new IntWritable(count);
    }
    public static class ClashRoyaleCleaningMapper
            extends Mapper<Text, CityWritable, IntWritable, IntWritable> {
        public static IntWritable count = new IntWritable(1);

        @Override
        protected void map(Text key, CityWritable value, Context context) throws IOException, InterruptedException {
            // Votre logique de mapping ici
            context.write(new IntWritable(value.population), count);
        }
    }
    public static class ClashRoyaleCleaningCombiner
            extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

        private static final ArrayList<IntWritable> intervals = new ArrayList<IntWritable>();

        static {
            intervals.add(new IntWritable(200000));
            intervals.add(new IntWritable(400000));
            intervals.add(new IntWritable(600000));
            intervals.add(new IntWritable(800000));
            intervals.add(new IntWritable(1000000));
            intervals.add(new IntWritable(5000000));
            intervals.add(new IntWritable(10000000));
            intervals.add(new IntWritable(20000000));
            intervals.add(new IntWritable(40000000));
        }

        public void reduce(IntWritable key, Iterable<IntWritable> values,Context context )
                throws IOException, InterruptedException {
            int i = 0;
            while(key.compareTo(intervals.get(i)) > 0){
                ++i;
            }
            context.write(intervals.get(i), getCount(values));
        }
    }
    public static class ClashRoyaleCleaningReducer
            extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void reduce(IntWritable key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            context.write(key, getCount(values));
        }
    }

    public static class CityWritable implements Writable, Cloneable {
        String country;
        String city;
        String accentCity;
        String region;
        int population;
        double latitude;
        double longitude;

        CityWritable() {

        }



        CityWritable(String country, String city, String accentCity, String region, int population, double latitude, double longitude) {
            this.country = country;
            this.city = city;
            this.accentCity = accentCity;
            this.region = region;
            this.population = population;
            this.latitude = latitude;
            this.longitude = longitude;
        }



        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(country);
            out.writeUTF(city);
            out.writeUTF(accentCity);
            out.writeUTF(region);
            out.writeInt(population);
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            country = in.readUTF();
            city = in.readUTF();
            accentCity = in.readUTF();
            region = in.readUTF();
            population = in.readInt();
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

      @Override
      public String toString() {
        return "Country: " + country +
                ", City: " + city +
                ", AccentCity: " + accentCity +
                ", Region: " + region +
                ", Population: " + population +
                ", Latitude: " + latitude +
                ", Longitude: " + longitude;
      }

        @Override
        public CityWritable clone() {
            try {
                CityWritable clone = (CityWritable) super.clone();
                clone.population = this.population;
                clone.country = this.country;
                clone.region = this.region;
                clone.city = this.city;
                clone.accentCity = this.accentCity;
                clone.latitude = this.latitude;
                clone.longitude = this.longitude;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleCleaning");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleCleaning.class);
        job.setMapperClass(ClashRoyaleCleaningMapper.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setCombinerClass(ClashRoyaleCleaningCombiner.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        job.setReducerClass(ClashRoyaleCleaningReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
