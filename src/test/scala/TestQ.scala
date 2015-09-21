import org.scalatest._

import scala.collection.immutable.Queue

class TestQ extends FlatSpec with Matchers {
  "Queue" should "append at end" in {
    val q = Queue[Int]()
    val qq = q :+ 1
    val qqq = qq :+ 2
    val (x, d) = qqq.dequeue
    x should be (1)
    d should be (q :+ 2)
  }

  it should "append several" in {
    val q = Queue[Int]()
    val qq = q ++ List(1, 2)
    val (x, d) = qq.dequeue
    x should be (1)
    d should be (q :+ 2)
  }
}
