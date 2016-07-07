package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable.Map

case class Contato(nome: String, email: String, telefone: String)

object Agenda {
  private var id = 0l
  private val contatos = Map[Long, Contato]()

  def inserir(contato: Contato) = {
    id = id + 1
    contatos += id -> contato
    id
  }
  def remover(id: Long) = {
      contatos -= id
  }
  def alterar(id: Long, contato: Contato) = {
      contatos(id) = contato
  }
  def todos = contatos.values.toList
  def get(id: Long) = contatos(id)
}

@Singleton
class HomeController @Inject() extends Controller {
import Agenda._
  private val formContato = Form(
      mapping(
        "nome"        -> nonEmptyText,
        "email"       -> email,
        "telefone"    -> text
      )(Contato.apply)(Contato.unapply)
    )

  def index = Action { 
    Redirect(routes.HomeController.list)
  }
  
  def list = Action { implicit request =>
    Ok(views.html.list(todos))
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
        inserir(contato)
        Redirect(routes.HomeController.list)
      }
    )
  }

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
        Redirect(routes.HomeController.list)
      }
    )
  }
  
  def delete(id: Long) = Action { implicit request =>
    remover(id)
    Redirect(routes.HomeController.list)
  }
}
