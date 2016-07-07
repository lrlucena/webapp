package controllers

import javax.inject.{ Inject, Singleton }
import models.{ Contato, ContatoDAO }
import play.api.data.Form
import play.api.data.Forms.{ email, longNumber, mapping, nonEmptyText }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, Controller }
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

@Singleton
class HomeController @Inject() (contatoDao: ContatoDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  private val formContato = Form(
    mapping(
      "id" -> longNumber,
      "nome" -> nonEmptyText,
      "email" -> email)(Contato.apply)(Contato.unapply))

  def index = Action {
    contatoDao.insert(new Contato(0, "Leonardo Lucena", "leonardo.lucena@email.com"))
    contatoDao.insert(new Contato(0, "Lucena", "leonardo@email.com"))
    Redirect(routes.HomeController.list())
  }

  def list(filtro: String) = Action.async { implicit request =>
    contatoDao.all(campo = "nome").map {
      contatos => Ok(views.html.list(contatos))
    }
  }

  def create = Action { implicit request =>
    Ok(views.html.create(formContato))
  }

  def save = Action { implicit request =>
    formContato.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.create(formWithErrors))
      },
      contato => {
        contatoDao.insert(contato).map {
          i => ()
        }
        Redirect(routes.HomeController.list())
      })
  }
  /*
  def index(s: String) = Action.async {
    contatoDao.insert(new Contato(0, "Leonardo", "leonardo@email.com"))
    contatoDao.insert(new Contato(0, "Lucena", "leonardo@email.com"))
    contatoDao.find(s).map {
      contatos => Ok(views.html.index(contatos))
    }
  } */

  def edit(id: Long) = Action { implicit request =>
    Ok(views.html.create(formContato))
  }

  def update(id: Long) = Action { implicit request =>
    formContato.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.create(formWithErrors))
      },
      contato => {
        //        lista = contato :: lista
        Redirect(routes.HomeController.list())
      })
  }

  def delete(id: Long) = Action { implicit request =>
    //  remover(id)
    Redirect(routes.HomeController.list())
  }

}
