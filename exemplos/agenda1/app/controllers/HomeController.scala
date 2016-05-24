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
  
  private def inteiro(campo: String)(implicit request: Request[AnyContent]) = texto(campo).toInt

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def todos(implicit request: Request[AnyContent]) = {
      val campos = request.body.asFormUrlEncoded.getOrElse(Map())
      val lista = for(campo <- campos) yield {
          val chave = campo._1
          val valor = campo._2.mkString(", ")
          s"${chave}: [${valor}]"
      }
      lista.mkString("\n")
  }

  def form = Action { implicit request =>
    val s = texto("nome")
    val r = texto("email")
    Ok(s"$s - $r\n$todos")
  }

}
