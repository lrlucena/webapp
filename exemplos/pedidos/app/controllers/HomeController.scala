package controllers

import javax.inject._
import play.api._
import play.api.mvc._

case class Cliente(nome: String, email: String){}
case class Item(codigo: Int, nome: String, descricao: String, preco: Double,quantidade: Int){}

@Singleton
class HomeController @Inject() extends Controller {

  def index = Action {
    val joao = Cliente("Joao", "joao@ifrn.edu.br")
    val item1 = Item(1,"Lapis", "Lapis Preto",1.00,10)
    val item2 = Item(4,"Borracha","Borracha para lapis",2.50,5)
    val itens = List(item1, item2)
    val total = itens.map{
      i => i.preco * i.quantidade
    }.sum
    Ok(views.html.index(joao, itens, total))
  }
}
