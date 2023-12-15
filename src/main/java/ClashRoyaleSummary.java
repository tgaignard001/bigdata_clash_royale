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

public class ClashRoyaleSummary {


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

            PlayerInfoWritable player1 = value.getPlayer1();
            PlayerInfoWritable player2 = value.getPlayer2();

            String newKey1 = sortCards(player1.getCards());
            String newKey2 = sortCards(player2.getCards());
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
        job.setJarByClass(ClashRoyaleSummary.class);
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
