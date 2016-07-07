package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class Principal @Inject() extends Controller {

  private def campo(chave: String,
    default: String = "")(implicit request: Request[_]) = {
    val form = request.body.asFormUrlEncoded.getOrElse(Map())
    form.get(chave).map {      // Some(Seq("i3"))
      case Seq(a) => a         // Some("i3")
      case _      => default
    }.getOrElse(default)       // "i3"
  }

  private def checkbox(chave: String)(implicit request: Request[AnyContent]) = {
    val form = request.body.asFormUrlEncoded.getOrElse(Map())
    form.get(chave)          // Some(Seq("teclado","Mouse"))
      .getOrElse(Seq())   // Seq("teclado","mouse")
  }

  def index = Action {
    Ok(views.html.index())
    // Ok(views.html.index2())
    // Ok(views.html.index3())
  }

  def selecionar = Action { implicit request =>
    val processador = campo("processador", "i3")
    val memoria = campo("memoria", "4")
    val opcionais = checkbox("opcionais")
    
    val precoPro = processador match {
      case "i5" => 200
      case "i7" => 500
      case _    => 0
    }
    
    val precoMem = memoria match {
      case "8"  => 150
      case "16" => 250 
      case _    => 0
    }
    val precoTec = if (opcionais.contains("teclado")) 80 else 0
    val precoMou = if (opcionais.contains("mouse")) 45 else 0
    val valor = 1500 + precoPro + precoMem + precoTec + precoMou
    
    Ok(views.html.configuracao(
        processador, memoria, opcionais, valor))
  }
}
