package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {

  private def lista[A](implicit request: Request[A]) =
    request.session.get(LISTA)
      .map{s => s.split(SEPARADOR).toList}
      .getOrElse(Nil)
      .sorted

  private val LISTA = "lista"
  private val SEPARADOR = ";"

  def index = Action { implicit request =>
    Ok(views.html.index(lista))
  }

  def inserir(produto: String) = Action { implicit request =>
    val novaLista = (produto::lista).mkString(SEPARADOR)
    Redirect("/")
      .withSession(request.session + (LISTA -> novaLista))
  }

  def remover(produto: String) = Action { implicit request =>
    val novaLista = lista.filter(_ != produto).mkString(SEPARADOR)
    Redirect("/")
      .withSession(request.session + (LISTA -> novaLista))
  }
}
