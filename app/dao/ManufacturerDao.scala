package dao

import anorm.SqlParser._
import anorm._
import model.Manufacturer
import play.api.Play.current
import play.api.db.DB


// passar para slick
// https://www.playframework.com/documentation/3.0.x/PlaySlick
// https://www.playframework.com/documentation/3.0.x/PlaySlickMigrationGuide
object ManufacturerDao {

  def merge(newRegister: Manufacturer): Manufacturer = {
    if (newRegister.id == 0)
      newRegister.id = insert(newRegister).getOrElse(0)
    else
      update(newRegister)

    return newRegister;
  }

  /**
   * This method returns the value of the auto_increment field when the stock is inserted
   * into the database table.
   * http://alvinalexander.com/scala/play-framework-anorm-insert-method-returns-auto-insert-id
   */
  def insert(newRegister: Manufacturer): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into aircraft (description, initials) values ({description}, {expiryDate}, {link})")
        .on("description" -> newRegister.description,
     //     "expiryDate" -> newRegister.expiryDate,
          "link" -> newRegister.link)
        .executeInsert()
    }
    id
  }

  def update(updateRegister: Manufacturer) = {
    DB.withConnection { implicit c =>
      SQL( """
        update manufacturer set
          description={description},
          link={link}
        where id={id}
           """)
        .on("description" -> updateRegister.description,
          "link" -> updateRegister.link,
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

  def rowMapper = {
    long("id") ~
      str("description") ~
      str("link") map {
      case id ~ description ~ link => Manufacturer(id, description, link)
    }
  }

  def getById(id: Long): Option[Manufacturer] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description,link
              FROM manufacturer
             WHERE manufacturer.id = {id}
          """.stripMargin)
          .on("id" -> id)
          .as(ManufacturerDao.rowMapper singleOpt)
    }
  }

  def getByInitials(link: String) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, link
              FROM manufacturer
             WHERE manufacturer.link = {link}
          """.stripMargin)
          .on("link" -> link)
          .as(ManufacturerDao.rowMapper *)
    }
  }

  def getByPagination(offset:Int, rowCount:Int) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, link
              FROM manufacturer
            order by description
             limit {offset}, {rowCount}
          """.stripMargin)
          .on("offset" -> offset,
              "rowCount" -> rowCount)
          .as(ManufacturerDao.rowMapper *)
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


  //case class Manufacturer(id:Long, name: String, emails: List[String], phones: List[String])
  //object Manufacturer {
  /* def rowMapper = {
     long("id") ~
       str("description") ~
       (str("email") ?) ~
       (str("number") ?) map {
       case id ~ name ~ email ~ number => ((id, name), email, number)
     }
   }*/
  //}

}
