package controllers

import javax.inject._
import play.api._
import play.api.mvc._


@Singleton
class HomeController @Inject() extends Controller {

  private def texto(campo: String)(implicit request: Request[AnyContent]) =
    request.body.asFormUrlEncoded.map {
      a => a(campo)(0)
    }.getOrElse("")
/*
  Some(Map("nome" -> List("eu"), "email" -> List("eu@ifrn.edu.br"),
  "opcionais"->List("monitor")))
  Some("eu")  => "eu"
  None        =>  ""

  Option[Map[String, Seq[String]]]
*/
  private def inteiro(campo: String)(implicit request: Request[AnyContent]) =
    texto(campo).toInt

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def todos(implicit request: Request[AnyContent]) = {
    val campos = request.body.asFormUrlEncoded.getOrElse(Map())
    val lista = for((chave,valor) <- campos) yield {
                  s"${chave}: [${valor.mkString(", ")}]"
                }
    lista.mkString("\n")
  }

  def form = Action { implicit request =>
    val s = texto("nome")
    val r = texto("email")
    Ok(s"$s - $r\n$todos")
  }
}
