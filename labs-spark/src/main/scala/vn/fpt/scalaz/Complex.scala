package vn.fpt.scalaz

import ComplexImplicits._

object ComplexImplicits {
  // implicit conversion
  implicit def DoubleComplex(value: Double) =
    new Complex(value, 0.0)

  implicit def TupleComplex(value: Tuple2[Double, Double]) =
    new Complex(value._1, value._2);

}

// Creating a class containg different method
case class Complex(val r: Double, val i: Double) {

  def +(that: Complex): Complex =
    Complex(this.r + that.r, this.i + that.i)

  def -(that: Complex): Complex =
    Complex(this.r - that.r, this.i + that.i)

  def unary_~ = Math.sqrt(r * r + i * i)

  override def toString = r + " + " + i + "i"

}

// Creating Onject
object Complex {

  val i = new Complex(0, 1);

  // Main method
  def main(args: Array[String]): Unit = {
    var a: Complex = Complex(6.0, 4.0)
    var b: Complex = Complex(1.0, 2.0)
    println(a)
    println(a + b)
    println(a - b)
    println(~b)

    var c = 5 + b
    println(c)
    var d = (3.0, 3.0) + c
    println(d)
  }
}