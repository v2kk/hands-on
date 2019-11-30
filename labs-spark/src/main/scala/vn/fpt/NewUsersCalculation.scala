package vn.fpt
import scala.io.Source

object NewUsersCalculation {
    def main(args: Array[String]): Unit = {

        val lines = Source.fromFile("../sample/stackoverflow_users.csv")
            .getLines().toArray
        val results = lines.drop(1)
        .map(line => {
            val arr = line.split(",")
            val date = arr(1).substring(0, 10)
            val userId = arr(0).toInt

            (date, 1)
        })
        .groupBy(_._1)
        .map{case (k, v) => (k, v.map(_._2).length)}
        .toList

        results.sortWith{ case (a, b) => a._1 > b._1}
        .take(20)
        .foreach{case (date, newUsers) => println(date + ": " + newUsers)}
    }
}


