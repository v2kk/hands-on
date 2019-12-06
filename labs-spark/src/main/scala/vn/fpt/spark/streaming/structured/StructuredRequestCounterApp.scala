package vn.fpt.spark.streaming.structured

import org.apache.hadoop.yarn.util.RackResolver
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.{DataFrame, SparkSession}
import vn.fpt.spark.ApacheAccess

object StructuredRequestCounterApp {

  def main(args: Array[String]): Unit = {

    Logger.getLogger(classOf[RackResolver]).getLevel
    Logger.getLogger("org.apache.kafka").setLevel(Level.OFF)
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf().setMaster("local[*]").setAppName("Request Counter")
    val spark = SparkSession
      .builder
      .config(conf)
      .appName("StructuredRequestCount")
      .getOrCreate()

    import spark.implicits._

    val rawDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "118.68.170.148:6667")
      .option("subscribe", "apache-access")
      .load()

    val requestDF = rawDF
      .select("value")
      .as[String]
      .map(line => ApacheAccess(line).getOrElse(ApacheAccess("", "", "", "")))
      .filter(_.ip != "")
      .groupBy("request")
      .agg(count("ip").as("no_of_requests"))

    val query = requestDF.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
