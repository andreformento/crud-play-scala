package dao

import anorm.SqlParser._
import anorm._
import model.Manufacturer
import play.api.Play.current
import play.api.db.DB

import java.util.Date

// http://manuel.bernhardt.io/2014/02/04/a-quick-tour-of-relational-database-access-with-scala/

object ManufacturerDao {

  def merge(newRegister: Manufacturer): Manufacturer = {
    if (newRegister.id == 0)
      newRegister.id = insert(newRegister).getOrElse(0)
    else
      update(newRegister)

    return newRegister;
  }

  def insert(newRegister: Manufacturer): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into manufacturer (description, link, expiry_date) values ({description}, {link}, {expiryDate})")
        .on("description" -> newRegister.description,
          "link" -> newRegister.link,
          "expiryDate" -> newRegister.expiryDate)
        .executeInsert()
    }
    id
  }

  def update(updateRegister: Manufacturer) = {
    DB.withConnection { implicit c =>
      SQL( """
        update manufacturer set
          description={description},
          link={link},
          expiry_date={expiryDate}
        where id={id}
           """)
        .on("description" -> updateRegister.description,
          "link" -> updateRegister.link,
          "expiryDate" -> updateRegister.expiryDate,
          "id" -> updateRegister.id)
        .execute()
    }
  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM manufacturer WHERE id = {id}")
        .on('id -> id)
        .executeUpdate()
      nRowsDeleted
    }
  }

  /*
SQL("Select name,indepYear from Country")().map { row =>
  row[String]("name") ->
  row[Option[Int]]("indepYear")
}
  * */

  def rowMapper = {
    get[Long]("id") ~
      get[String]("description") ~
      get[Option[String]]("link") ~
      get[Option[Date]]("expiry_date") map {
      case id ~ description ~ link ~ expiryDate => Manufacturer(id, description, link, expiryDate)
    }
  }

  /*
  def rowMapper = {
    long("id") ~
      str("description") ~
      str("link") ~
      date("expiry_date") map {
      case id ~ description ~ link ~ expiryDate => Manufacturer(id, description, link, expiryDate)
    }
  }*/

  def getById(id: Long): Option[Manufacturer] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, link, expiry_date
              FROM manufacturer
             WHERE manufacturer.id = {id}
          """.stripMargin)
          .on("id" -> id)
          .as(rowMapper singleOpt)
    }
  }

  def getByInitials(link: String) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, link, expiry_date
              FROM manufacturer
             WHERE manufacturer.link = {link}
          """.stripMargin)
          .on("link" -> link)
          .as(rowMapper *)
    }
  }

  def getByPagination(offset: Int, rowCount: Int) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, link, expiry_date
              FROM manufacturer
            order by description
             limit {offset}, {rowCount}
          """.stripMargin)
          .on("offset" -> offset,
            "rowCount" -> rowCount)
          .as(rowMapper *)
    }
  }

  def getCount: Int = {
    val result: Int =
      DB.withConnection { implicit c =>
        SQL("Select count(1) as c from manufacturer")
          .as(scalar[Int].single)
      }

    result
  }

}
