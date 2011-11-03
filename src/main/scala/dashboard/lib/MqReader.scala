package dashboard.lib

import org.zeromq.ZMQ
import akka.actor.ActorRef


class MqReader(actor: ActorRef) {
  var keepRunning = true
  
  def stop() {
    println("waiting for stop...")
    keepRunning = false
    Thread.sleep(2000)
    println("stop has hopefully happened")
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
        // remove "self refreshes"
        .filterNot { isSelfRefresh }
        // remove common filter
        .filterNot { e =>
          e.path.endsWith(".ico") || e.path.endsWith(".xml") || e.path.endsWith(".swf") ||
            e.path.endsWith(".html") || e.path.endsWith("/json") || e.path == "/_" ||
            e.path.startsWith("/global/adcode/generate")
        }

      event.foreach { actor ! }
    } while (keepRunning)

    sub.close()
    context.term()
    
    println("Stopped!")
  }


  def isSelfRefresh(e: Event) = e.referrer.exists(_.startsWith("http://www.guardian.co.uk" + e.path))

}