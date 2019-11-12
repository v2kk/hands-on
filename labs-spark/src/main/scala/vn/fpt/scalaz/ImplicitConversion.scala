package vn.fpt.scalaz

import vn.fpt.scalaz.DateUtils._

object ImplicitConversion {


  def main(args: Array[String]): Unit = {
    val date = "2019-11-12 23:00:00"

    println(date.toEpochSecond)
  }
}

