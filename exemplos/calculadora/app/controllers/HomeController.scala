package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {

  def index = Action {
    Ok(views.html.somar(0, 0, 0))
  }
  def somar(x: Int, y: Int) = Action.apply {
    val resultado = x + y
    Ok(views.html.somar(x, y, resultado)) 
  }
  def multiplicar = TODO

  def uol(arq: String) = Action { request =>
    Redirect(s"http://www.${arq}.com.br")
  }

  def pedido = TODO

}
