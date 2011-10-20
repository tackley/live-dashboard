package dashboard.lib

import akka.actor.Actor._
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit


object Backend {
  val listener = actorOf[EventListener].start()
  Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)

  val mqReader = new MqReader(listener)

  def start() {
    spawn {
      mqReader.start()
    }

//    while (2 + 2 != 42) {
//      Thread.sleep(5000)
//
//      for (stream <- (listener ? GetClickStream()).as[ClickStream]) {
//        println("\n** clickstream size is " + stream.clicks.size + " age (ms) " + stream.ageMs)
//        val topTen = Calculator.calcTopTenPaths(stream)
//        topTen map { _.summary } foreach println
//
//        println("total %.1f%% " format topTen.map(_.percent).sum)
//      }
//
//    }

  }

  def stop() {
    mqReader.stop()
    Scheduler.shutdown()
    listener.stop()
  }

  def clickStream = (listener ? GetClickStream()).as[ClickStream].get

  def currentTopTen = Calculator.calcTopTenPaths(clickStream)
}