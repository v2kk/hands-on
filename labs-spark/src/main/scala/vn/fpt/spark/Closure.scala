package vn.fpt.spark

import org.apache.spark.{SparkConf, SparkContext}

object Closure {
    def main(args: Array[String]): Unit = {

        // create spark context
        val conf = new SparkConf().setAppName("Word Count").setMaster("local[1]")
        val sc = new SparkContext(conf)

        var counter = 0
        val rdd = sc.parallelize(Range(0, 1000))

        rdd.foreach(x => {
            println(counter)
            counter += x
        })

        println("Counter value: " + counter)

        sc.stop()
    }
}