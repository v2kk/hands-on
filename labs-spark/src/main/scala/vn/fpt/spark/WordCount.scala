package vn.fpt.spark

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
    def main(args: Array[String]): Unit = {

        // create spark context
        val conf = new SparkConf().setAppName("Word Count")
        val sc = new SparkContext(conf)

        // input file location
        val inFile = args(0)
        val outFile = args(1)

        // load the data as text
        val textFile = sc.textFile(inFile)

        textFile
        .map(line => line.split("\\s+"))
        .flatMap(x => x)
        .map(word => (word, 1))
        .reduceByKey((a, b) => a + b)
        .saveAsTextFile(outFile)

        sc.stop()
    }
}