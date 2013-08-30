package uk.co.bhyland.flyweight

import java.nio.ByteBuffer
import java.nio.charset.Charset

import scala.collection.mutable.ArrayBuffer

trait ByteSource {
  def readInt(position: Long): Int
  def readString(position: Long, length: Int): String
}

class ByteBufferByteSource(bytes: ByteBuffer) extends ByteSource {
  def readInt(offset: Long) = bytes.getInt(offset.toInt)
  def readString(position: Long, length: Int) = new String(bytes.array(), position.toInt, length, Charset.forName("UTF-8"))
}

object ByteSource {
  def from(bytes: Array[Byte]): ByteSource = new ByteBufferByteSource(ByteBuffer.wrap(bytes))
}

trait Position {
  def position: Long
}

trait Moveable {
  def moveNext: Unit
}

trait ReaderFactory {
  def intReader: Reader[Int]
  def stringReader: Reader[String]
}

trait Flyweight extends Moveable with Position {
  val flyweight: Moveable with Position
  
  override def position: Long = flyweight.position
  override def moveNext: Unit = flyweight.moveNext
}

trait PositionListener {
  def onPositionChanged: Unit
}

abstract class Positioned extends Moveable with Position {
  private var _position: Long = 0L
  override def position: Long = _position
  
  private val listeners = ArrayBuffer[PositionListener]()
  def registerListener(listener: PositionListener) = {
    listeners.append(listener)
  }
  
  override def moveNext: Unit = {
    _position = nextPosition
    listeners.foreach(Positioned.doNotify)
  }
  
  def length: Long
  
  def nextPosition: Long = position + length
}

object Positioned {
  val doNotify: PositionListener => Unit = (pl: PositionListener) => pl.onPositionChanged
}

class CompositeFlyweight(bytes: ByteSource) extends Positioned {
  private val readers = ArrayBuffer[Reader[_]]()
  
  override def length: Long = readers.foldLeft(0L)(CompositeFlyweight.addLength)
  
  def add[A](r: Reader[A]) = {
    readers.append(r)
    r
  }
  
  def <-:[A](builder: ReaderBuilder[A]): Reader[A] = {
    add(builder.build(bytes, this))
  }
}

object CompositeFlyweight {
  val addLength: (Long, Reader[_]) => Long = (sum: Long, r: Reader[_]) => sum + r.length
}

trait ReaderBuilder[A] {
  def build(bytes: ByteSource, context: Positioned) : Reader[A]
}

object ReaderBuilder {
  
  def intReader = new ReaderBuilder[Int] {
    def build(bytes: ByteSource, context: Positioned) = Reader.intReader(bytes, context)
  }
  
  def stringReader = new ReaderBuilder[String] {
    def build(bytes: ByteSource, context: Positioned) = Reader.stringReader(bytes, context)
  }
  
  implicit def reader2Memoizable[A <: AnyRef](r: ReaderBuilder[A]) = new {
    def memoized = new ReaderBuilder[A] {
      def build(bytes: ByteSource, context: Positioned) = {
        val mr = new MemoizingReader[A](r.build(bytes, context))
        context.registerListener(mr)
        mr
      }
    }
  }
}

trait Reader[A] {
  def length: Long
  def read: A
}

object Reader {
  def intReader(bytes: ByteSource, position: Position) = new Reader[Int] {
    def length = 4L
    def read = bytes.readInt(position.position)
  }
  
  def stringReader(bytes: ByteSource, position: Position) = new Reader[String] {
    def strLength = bytes.readInt(position.position)
    def length = strLength + 4L
    def read = bytes.readString(position.position + 4L, strLength)
  }
}

class MemoizingReader[A](r: Reader[A]) extends Reader[A] with PositionListener {
  private var _memoized = false
  private var _value: A = null.asInstanceOf[A]
  private var _length: Long = 0L
  
  def length = if(_memoized) _length else {
    read
    _length
  }
  
  def read: A = if(_memoized) _value else {
    _value = r.read
    _length = r.length
    _memoized = true
    _value
  }
  
  override def onPositionChanged = {
    _memoized = false
  }
}
