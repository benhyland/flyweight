package uk.co.bhyland.flyweight

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.google.monitoring.runtime.instrumentation.AllocationRecorder
import com.google.monitoring.runtime.instrumentation.Sampler

import Fixtures._

class FlyweightAllocationTest extends FunSuite with ShouldMatchers {

  test("flyweight should not allocate anything when reading primitives") {
    val a = byteArrayFromInts(1,2,3,4,5,6)
    val bytes = ByteSource.from(a)
    
    val fly = singleIntFlyweight(bytes)
    
    val sampler = allocationSampler
    
    sampler.enabled = true
    new String("allocate once to ensure sampler is working")

    var i = 0
    while(i < 6) {
      fly.intField
      fly.moveNext
      i += 1
    }

    sampler.enabled = false
    
    sampler.getCount should be (1)
  }
  
  def allocationSampler = {
    val sampler = new CountingSampler()
    AllocationRecorder.addSampler(sampler)
    sampler
  }
}

class CountingSampler(var enabled: Boolean = false) extends Sampler {
  var _count = 0
  def getCount = _count
  override def sampleAllocation(count: Int, desc: String, obj: AnyRef, size: Long) {
    if(enabled) {
	    _count += 1
	    print("******* saw allocation of ")
	    print(desc)
	    println(" *******")
    }
  }
}
