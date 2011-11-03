package dashboard

import lib.Event
import org.specs2.mutable.Specification
import org.zeromq.ZMQ
import org.specs2.execute.Success


class ZeroMqTest extends Specification {
  "ZeroMQ" should {

//    "parse" in {
//      val s = """69.121.215.5 - - [20/Oct/2011:12:31:07 +0100] "GET /world HTTP/1.1" 200 25539 "-" "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)" "usa;ct;fairfield;cable; 41.175;-73.281;c" "-;-;-" "-""""
//
//      val event = Event.fromApacheLog(s)
//
//      event must_!= None
//    }

    "be able to read published stuff" in {
      val context = ZMQ.context(1)
      val sub = context.socket(ZMQ.SUB)

      sub.connect("tcp://localhost:5536")
      sub.subscribe(Array.empty)
      sub.setHWM(50)

      for (i <- 1 to 20) {
        val result = new String(sub.recv(0))

        println("got: " + result)

        val event = Event.fromApacheLog(result)
        println("-> " + event)
      }

      sub.close()
      context.term()

      Success()
    }
  }

}