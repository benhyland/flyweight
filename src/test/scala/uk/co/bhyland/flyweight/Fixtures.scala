package uk.co.bhyland.flyweight

import ReaderBuilder.intReader
import scala.Array.canBuildFrom

object Fixtures {
  
  trait SingleIntInterface {
    def intField: Int
  }
  
  trait SingleStringInterface {
    def stringField: String
  }
  
  def intBytes(i: Int) = Array(
    (i >> 24).toByte,
    (i >> 16).toByte,
    (i >>  8).toByte,
    (i >>  0).toByte
  )
  
  def byteArrayFromInts(is: Array[Int]): Array[Byte] = byteArrayFromInts(is:_*)
  
  def byteArrayFromInts(is: Int*): Array[Byte] = {
    is.toArray.flatMap(intBytes)
  }
  
  def byteArrayFromStrings(ss: String*): Array[Byte] = {
    def stringBytes(s: String) = Array.concat(
      intBytes(s.length()),
      s.getBytes("UTF-8")
    )
    
    ss.toArray.flatMap(stringBytes)
  }
  
  import ReaderBuilder._

  def singleIntFlyweight(bytes: ByteSource) = new Flyweight with SingleIntInterface {
    val flyweight = new CompositeFlyweight(bytes)
    val _intField = intReader <-: flyweight
    override def intField: Int = _intField.read
  }
  
  def singleStringFlyweight(bytes: ByteSource) = new Flyweight with SingleStringInterface {
    val flyweight = new CompositeFlyweight(bytes)
    val _stringField = stringReader <-: flyweight
    override def stringField: String = _stringField.read
  }
  
  def memoizingSingleStringFlyweight(bytes: ByteSource) = new Flyweight with SingleStringInterface {
    val flyweight = new CompositeFlyweight(bytes)
    val _stringField = stringReader.memoized <-: flyweight
    override def stringField: String = _stringField.read
  }
}
