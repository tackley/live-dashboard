package dashboard

import org.zeromq.ZMQ
import akka.actor.ActorRef


object MqReader {

  def run(actor: ActorRef) {
    val context = ZMQ.context(1)
    val sub = context.socket(ZMQ.SUB)

    sub.connect("tcp://localhost:5536")
    sub.subscribe(Array.empty)
    sub.setHWM(50)

    do {
      val result = new String(sub.recv(0))

      val event = Event.fromApacheLog(result)
        // remove failures
        .filter { _.responseCode == 200 }
        // remove "self refreshes"
        .filterNot { e => e.referrer == Some(e.path) }
        // remove common filter
        .filterNot { e => e.path.endsWith(".ico") || e.path.endsWith(".xml") || e.path.endsWith(".swf") || e.path.endsWith(".html") }



      event.foreach { actor ! }
    } while (2 + 2 != 5)

    sub.close()
    context.term()
  }

}