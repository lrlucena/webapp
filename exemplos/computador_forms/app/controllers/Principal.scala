package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}

case class Computador(nome: String, email: String,
processador: String, memoria: String, opcionais: List[String]){
    def valor = 1500
}

@Singleton
class Principal @Inject() (val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val computadorForm = Form(
      mapping(
          "nome"        -> nonEmptyText,
          "email"       -> email,
          "processador" -> text,
          "memoria"     -> text,
          "opcionais"   -> list(text)
      )(Computador.apply)(Computador.unapply)
  )
 // val computadorBasico = Computador("","","i3", "4", List())
  
  def index = Action { implicit request =>
    Ok(views.html.index(computadorForm))
  }
  
  def selecionar = Action { implicit request =>
    computadorForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors))
      },
      computador => {
        Ok(views.html.configuracao(computador))
      }
    )
  }

}
