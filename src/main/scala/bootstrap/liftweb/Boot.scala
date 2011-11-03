package bootstrap.liftweb

import net.liftweb.http._
import dashboard.lib.Backend
import util.Properties
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext


class Boot {
  def boot() {
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.addToPackages("dashboard")

    Backend.start()

    LiftRules.unloadHooks.append(Backend.stop _)
  }
}

object Boot extends App {
  val port = Properties.envOrElse("PORT", "8080").toInt

  val server = new Server(port)

  val context = new WebAppContext
  context.setWar("src/main/webapp")
  context.setContextPath("/")

  server.setHandler(context)

  server.start();
  server.join();
}