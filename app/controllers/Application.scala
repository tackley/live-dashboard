package controllers

import play.api._
import play.api.mvc._
import lib.Backend

object Application extends Controller {
  
  def index = Action { Ok(views.html.index()) }

  def top10 = Action { Ok(views.html.top10(Backend.currentLists.get.all)) }
  def top10chart = Action { Ok(views.html.snippets.top10chart(Backend.currentLists.get.all)) }

  def top20 = Action { Ok(views.html.top20(Backend.currentLists.get))}
  def top20chart = Action { Ok(views.html.snippets.top20chart(Backend.currentLists.get)) }

  def details = Action { Ok(views.html.details(Backend.currentLists.get.everything)) }

  def search = Action { Ok(views.html.search()) }
  
}