package vn.fpt.spark.streaming.dstream

import org.apache.hadoop.yarn.util.RackResolver
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import vn.fpt.spark.ApacheAccess

object RequestEveryMinuteApp {

  def main(args: Array[String]): Unit = {

    // turn off all logger
    Logger.getLogger(classOf[RackResolver]).getLevel
    Logger.getLogger("org.apache.kafka").setLevel(Level.OFF)
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    // instantialize spark config & streaming context
    val conf = new SparkConf().setMaster("local[*]").setAppName("Request Counter")
    val ssc = new StreamingContext(conf, Seconds(60))
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "118.68.170.148:6667",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "vinhdp4",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false"
    )

    // topic to be consumed
    val topics = Seq("apache-access")

    // read data from kafka
    val stream = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))


    import spark.implicits._

    stream.foreachRDD(rdd => {

      // for each micro batch
      rdd.map(line => ApacheAccess(line.value()))
        .filter(_.isDefined)
        .map(_.get)
        .toDS
        .groupBy("request")
        .agg(count("ip").as("no_of_requests"))
        .show(false)

    })

    // start streaming
    ssc.start()
    ssc.awaitTermination()
  }
}
