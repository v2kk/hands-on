package vn.fpt;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.lang.Integer;
import java.lang.NumberFormatException;

public class NewUsersCalc {
    public static class UserMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // split a line into words
            String[] arr = value.toString().split(",");
            String date = arr[1].substring(0, 10);
            int userId = -1;
            try {
                userId = Integer.parseInt(arr[0]);
            } catch (NumberFormatException ex) {
                // do nothing
            }
            if(userId != -1) {
                context.write(new Text(date), new IntWritable(userId));
            }
        }
    }

    public static class UserReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            // sum up counts for the key
            int sum = 0;
            for (IntWritable value : values) {
                sum = sum + 1;
            }
            // output (word, count)
            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        // instantiate a configuration
        Configuration configuration = new Configuration();

        // instantiate a job
        Job job = Job.getInstance(configuration, "Stackoverflow New Users");

        // set job parameters
        job.setJarByClass(NewUsersCalc.class);
        job.setMapperClass(NewUsersCalc.UserMapper.class);
        //job.setCombinerClass(NewUsersCalc.UserReducer.class);
        job.setReducerClass(NewUsersCalc.UserReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // set io paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}