package dashboard.lib

import akka.actor.Actor._
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit


object Backend {
  val listener = actorOf[EventListener].start()
  Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)
  Scheduler.schedule(listener, UpdateFrontend(), 5, 5, TimeUnit.SECONDS)

  val mqReader = new MqReader(listener)

  def start() {
    spawn {
      mqReader.start()
    }
  }

  def stop() {
    mqReader.stop()
    Scheduler.shutdown()
    listener.stop()
  }

  def clickStream = (listener ? GetClickStream()).as[ClickStream].get

  def currentTopTen = Calculator.calcTopPaths(clickStream)
}