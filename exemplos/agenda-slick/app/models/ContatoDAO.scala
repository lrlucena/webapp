package models

import javax.inject.Inject
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

class ContatoDAO @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val contatos = TableQuery[ContatosTable]

  val bd = db.run[Seq[Contato]] _

  def all(campo: String = "", asc: Boolean = true) = bd {
    val a = (campo, asc) match {
      case ("email", true)  => contatos sortBy { _.email }
      case ("email", false) => contatos sortBy { _.email.desc }
      case ("nome", true)   => contatos sortBy { _.nome }
      case ("nome", false)  => contatos sortBy { _.nome.desc }
      case _                => contatos
    }
    a.result
  }

  def get(id: Long) = bd {
    contatos.filter(_.id === id).result
  }

  def find(s: String) = bd {
    contatos filter { _.nome like s"%$s%" } sortBy { _.nome } result
  }

  def insert(contato: Contato) = db.run {
    contatos.insertOrUpdate(contato)
  }

  def delete(id: Long) = db.run {
    contatos.filter(_.id === id).delete
  }

  def update(contato: Contato) = db.run {
    contatos.insertOrUpdate(contato)
  }

  class ContatosTable(tag: Tag) extends Table[Contato](tag, "CONTATO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def nome = column[String]("NOME")
    def email = column[String]("EMAIL")

    def * = (id, nome, email) <> (Contato.tupled, Contato.unapply)
  }
}
