import akka.actor.ActorSystem
import org.specs2.matcher.EventuallyMatchers
import org.specs2.mutable.Specification
import akka.agent._


class HackingTest extends Specification with EventuallyMatchers {
  "agents" should {
    "do what I think" in {
      implicit val s = ActorSystem("test")

      val agent = Agent(17)

      agent send 6

      agent() must eventually(beEqualTo(6))
    }
  }

}
