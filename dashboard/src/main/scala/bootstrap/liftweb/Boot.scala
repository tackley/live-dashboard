package bootstrap.liftweb

import net.liftweb.http._
import dashboard.lib.Backend


class Boot {
  def boot() {
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.addToPackages("dashboard")

    Backend.start()

    LiftRules.unloadHooks.append(Backend.stop _)
  }
}