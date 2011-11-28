package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action { Ok(views.html.index()) }

  def top10 = Action { Ok(views.html.top10()) }

  def top20 = TODO

  def details = TODO
  
}