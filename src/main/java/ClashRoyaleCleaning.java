import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;


public class ClashRoyaleCleaning {

    public static class ClashRoyaleCleaningMapper
            extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            JSONObject game = null;
            try {
                game = new JSONObject(value.toString());

                String date = game.getString("date");
                String round = game.getString("round");
                String player1 = game.getString("player");
                String player2 = game.getString("player2");
                context.write(new Text(date+round+player1+player2),new IntWritable(0));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ClashRoyaleCleaningReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values,Context context )
                throws IOException, InterruptedException {
            context.write(key, values.iterator().next());
        }
    }

    /*
    public static class GameWritable implements Writable, Cloneable {
        String country;
        String city;
        String accentCity;
        String region;
        int population;
        double latitude;
        double longitude;

        GameWritable() {

        }



        GameWritable(String country, String city, String accentCity, String region, int population, double latitude, double longitude) {
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
        public GameWritable clone() {
            try {
                GameWritable clone = (GameWritable) super.clone();
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
*/
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClashRoyaleCleaning");
        job.setNumReduceTasks(1);
        job.setJarByClass(ClashRoyaleCleaning.class);
        job.setMapperClass(ClashRoyaleCleaningMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setReducerClass(ClashRoyaleCleaningReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
