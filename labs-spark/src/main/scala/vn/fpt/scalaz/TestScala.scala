package vn.fpt.scalaz
import java.time.LocalDate

object TestScala {
    
    case class User(name: String, birthday: LocalDate)
    
    def main(args: Array[String]): Unit = {

        val user1 = User("vinhdp4", LocalDate.of(1993, 12, 27))
        val user2 = User("vinhdp45", LocalDate.of(1993, 12, 27))

        println(user1 == user2)
    }
}