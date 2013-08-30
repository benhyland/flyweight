package uk.co.bhyland.flyweight

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import Fixtures._

class FlyweightTest extends FunSuite with ShouldMatchers {

  test("flyweight should read several ints from an array") {
    
    val a = byteArrayFromInts(1,2,-1)
    val bytes = ByteSource.from(a)
    
    val fly = singleIntFlyweight(bytes)
    
    fly.intField should be (1)
    
    fly.moveNext
    
    fly.intField should be (2)
    
    fly.moveNext
    
    fly.intField should be (-1)
  }
  
  
  test("flyweight should read several strings from an array") {
    
    val a = byteArrayFromStrings("bob", "fred", "", "bill")
    val bytes = ByteSource.from(a)
    
    val fly = singleStringFlyweight(bytes)
    
    fly.stringField should be ("bob")
    
    fly.moveNext
    
    fly.stringField should be ("fred")
    
    fly.moveNext
    
    fly.stringField should be ("")
    
    fly.moveNext
    
    fly.stringField should be ("bill")
  }
  
  test("memoized flyweight should only read once at a given position") {
    
    val a = byteArrayFromStrings("bob", "bob")
    val bytes = ByteSource.from(a)
    
    val fly = memoizingSingleStringFlyweight(bytes)
    
    val bob1 = fly.stringField
    val bob2 = fly.stringField
    
    bob1 should be theSameInstanceAs (bob2)
    
    fly.moveNext
    val bob3 = fly.stringField
    
    bob1 should be (bob3)
    bob1 should not be theSameInstanceAs (bob3)
  }
}
