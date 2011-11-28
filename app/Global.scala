import lib.Backend
import play.api._




object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Starting...")
    Backend.start()
    Logger.info("Started")
  }

  override def onStop(app: Application) {
    Logger.info("Stopping...")
    Backend.stop()
    Logger.info("Stopped")

  }
}