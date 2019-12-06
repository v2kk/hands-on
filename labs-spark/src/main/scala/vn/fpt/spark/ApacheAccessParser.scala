package vn.fpt.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

case class ApacheAccess(ip: String, date: String, method: String, request: String) {

  override def toString() = {
    s"$date - $ip - $method - $request"
  }
}

object ApacheAccess {

  def apply(line: String): Option[ApacheAccess] = {

    val ip = """(\d+.\d+.\d+.\d+)"""
    val date = """(\d+\/.*\/\d+:\d+:\d+:\d+) \+0100"""
    val method = """(GET|POST)"""
    val req = s"""(\\/[^ ]*)"""

    val urlRegex = s"""$ip - - \\[$date\\] "$method $req.*""".r

    line match {
      case urlRegex(ip, date, method, req) => Some(ApacheAccess(ip, date, method, req))
      case _ => None
    }
  }

  def main(args: Array[String]): Unit = {

    // http://www.almhuette-raith.at/apache-log/access.log
    val rawSeq = Seq(
      """109.169.248.247 - - [12/Dec/2015:18:25:11 +0100] "GET /administrator/ HTTP/1.1" 200 4263 "-" "Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0" "-"""",
      """109.169.248.247 - - [12/Dec/2015:18:25:11 +0100] "POST /administrator/index.php HTTP/1.1" 200 4494 "http://almhuette-raith.at/administrator/" "Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0" "-"""",
      """191.182.199.16 - - [12/Dec/2015:19:02:35 +0100] "GET /modules/mod_bowslideshow/tmpl/css/bowslideshow.css HTTP/1.1" 200 1725 "http://almhuette-raith.at/" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)""",
      """46.116.249.142 - - [12/Dec/2015:19:28:28 +0100] "POST /administrator/index.php HTTP/1.1" 200 4494 "http://almhuette-raith.at/administrator/" "Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0" "-"""",
      """46.116.249.142 - - [12/Dec/2015:19:28:28 +0100] "PUT /administrator/index.php HTTP/1.1" 200 4494 "http://almhuette-raith.at/administrator/" "Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0" "-""""
    )

    val result = rawSeq.map(line => ApacheAccess(line))

    result.foreach(println)
  }
}

object ApacheAccessParser {

  def main(args: Array[String]): Unit = {

    // create spark context
    val conf = new SparkConf().setAppName("Word Count").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // input file location
    val inFile = "/home/vinhdp/workspaces/fpt/training/sample/access-small.log"
    //val outFile = args(1)

    // load the data as text
    val textFile: RDD[String] = sc.textFile(inFile)

    val rdd = textFile
      .map(line => ApacheAccess(line))
      .filter(_.isDefined)
      .map(_.get)
      .map(a => a.date.substring(0, 11) -> a.ip)
      .distinct
      .map(x => x._1 -> 1)
      .reduceByKey(_ + _)
      .take(10)
      .foreach(println)



    sc.stop()
  }
}
