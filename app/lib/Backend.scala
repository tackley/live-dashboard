package lib

import akka.actor.Actor._
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit


object Backend {
  val listener = actorOf[EventListener].start()

  val mqReader = new MqReader(listener)

  def start() {
    Scheduler.restart()
    Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)
    spawn {
      mqReader.start()
    }
  }

  def stop() {
    Scheduler.shutdown()
    mqReader.stop()
    listener.stop()
  }

  def clickStream = (listener ? GetClickStream()).as[ClickStream].get

  def currentTopTen = Calculator.calcTopPaths(clickStream)
}