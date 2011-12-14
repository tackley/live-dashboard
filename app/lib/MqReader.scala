package lib

import org.zeromq.ZMQ
import akka.actor.ActorRef
import play.api.Logger

object MqReader {
  // 10 is a magic number here : I know we're sampling 2 servers, and we have 20 in total
  val SCALE_TO_FULL_SITE = 10
}

//
// NB: akka 1.3 supports 0MQ actors, should use that instead
//
class MqReader(consumers: List[ActorRef]) {
  val logger = Logger(getClass)
  var keepRunning = true
  
  def stop() {
    logger.info("waiting for stop...")
    keepRunning = false
    Thread.sleep(500)
    logger.info("stop has hopefully happened")
  }
  
  def start() {
    val context = ZMQ.context(1)
    val sub = context.socket(ZMQ.SUB)

    sub.connect("tcp://localhost:5100")
    sub.connect("tcp://localhost:5200")
    sub.subscribe(Array.empty)
    sub.setHWM(50)

    do {
      val result = new String(sub.recv(0))

      val event = Event.fromApacheLog(result)
        // remove failures
        .filter { _.responseCode == 200 }
        // only interested in "GET"'s
        .filter { _.method == "GET" }
        // remove "self refreshes"
        .filterNot { isSelfRefresh }
        // remove common filter
        .filterNot { e =>
          e.path.endsWith(".ico") || e.path.endsWith(".xml") || e.path.endsWith(".swf") ||
            e.path.endsWith(".html") || e.path.endsWith("/json") || e.path == "/_" ||
            e.path.startsWith("/global/adcode/generate")
        }

      for (e <- event; a <- consumers) a ! e

    } while (keepRunning)

    sub.close()
    context.term()
    
    logger.info("Stopped!")
  }


  def isSelfRefresh(e: Event) = e.referrer.exists(_.startsWith("http://www.guardian.co.uk" + e.path))

}
