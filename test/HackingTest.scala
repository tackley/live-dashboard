import akka.actor.ActorSystem
import org.specs2.matcher.EventuallyMatchers
import org.specs2.mutable.Specification
import akka.agent._
import play.api.libs.ws.WS


class HackingTest extends Specification with EventuallyMatchers {
  "api call" should {
    "work" in {
      val p = WS.url("http://content.guardianapis.com/uk/uk.json").get().map { r =>
        (r.json \ "response" \ "leadContent" \\ "id").map(_.as[String])
      }
      
      val theVal = p.await

      println(theVal)

      1 + 1 must_==(3)
    }
  }

}
