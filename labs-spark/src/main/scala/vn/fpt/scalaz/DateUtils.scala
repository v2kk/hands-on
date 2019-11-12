package vn.fpt.scalaz

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object DateUtils {

  implicit class StringImprovements(val date: String) {
      def toEpochSecond = {
          val locaDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
          locaDate.toEpochSecond(ZoneOffset.ofHours(7))
      }
  }
}
