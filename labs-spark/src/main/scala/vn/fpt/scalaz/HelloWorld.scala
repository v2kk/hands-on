package vn.fpt.scalaz

import scala.io.Source


object HelloWorld {

  def main(args: Array[String]): Unit = {


    val lines = Source.fromFile("../sample/log.log").getLines()
    val r = lines
      .flatMap(s => s.split("\\s+"))
      .map(s => (s, 1))
      .toList
      .groupBy(_._1)
      .map{case (k, v) => (k, v.length)}
      .foreach(println)

    println(r)
    println("Hello World")
  }
}