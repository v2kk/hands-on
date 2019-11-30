package vn.fpt.spark

import org.apache.spark.{SparkConf, SparkContext}

object Accumulator {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Word Count").setMaster("local[1]")
    val sc = new SparkContext(conf)

    var counter = 0
    val rdd = sc.parallelize(Range(0, 1000))

    val accum = sc.longAccumulator("My Accumulator")

    rdd.foreach(x => {
      println(counter)
      accum.add(x)
      counter += x
    })

    println("Counter value: " + counter)
    println("Accumulator: " + accum.value)

    sc.stop()
  }
}
