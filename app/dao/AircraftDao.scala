package dao

import anorm.SqlParser._
import anorm._
import model.Aircraft
import play.api.Play.current
import play.api.db.DB


// passar para slick
// https://www.playframework.com/documentation/3.0.x/PlaySlick
// https://www.playframework.com/documentation/3.0.x/PlaySlickMigrationGuide
object AircraftDao {

  def merge(newRegister: Aircraft): Aircraft = {
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
  def insert(newRegister: Aircraft): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into aircraft (description, initials) values ({description}, {initials})")
        .on("description" -> newRegister.description,
          "initials" -> newRegister.initials)
        .executeInsert()
    }
    id
  }

  def update(updateRegister: Aircraft) = {
    DB.withConnection { implicit c =>
      SQL( """
        update aircraft set
          description={description},
          initials={initials}
        where id={id}
           """)
        .on("description" -> updateRegister.description,
          "initials" -> updateRegister.initials,
          "id" -> updateRegister.id)
        .execute()
    }
  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM aircraft WHERE id = {id}")
        .on('id -> id)
        .executeUpdate()
      nRowsDeleted
    }
  }

  def rowMapper = {
    long("id") ~
      str("description") ~
      str("initials") map {
      case id ~ description ~ initials => Aircraft(id, description, initials)
    }
  }

  def getById(id: Long): Option[Aircraft] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description,initials
              FROM aircraft
             WHERE aircraft.id = {id}
          """.stripMargin)
          .on("id" -> id)
          .as(AircraftDao.rowMapper singleOpt)
    }
  }

  def getByInitials(initials: String) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, initials
              FROM aircraft
             WHERE aircraft.initials = {initials}
          """.stripMargin)
          .on("initials" -> initials)
          .as(AircraftDao.rowMapper *)
    }
  }

  def getByPagination(offset:Int, rowCount:Int) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, description, initials
              FROM aircraft
            order by description
             limit {offset}, {rowCount}
          """.stripMargin)
          .on("offset" -> offset,
              "rowCount" -> rowCount)
          .as(AircraftDao.rowMapper *)
    }
  }

  def getCount: Int = {
    val result: Int =
      DB.withConnection { implicit c =>
        SQL("Select count(1) as c from aircraft")
          .as(scalar[Int].single)
    }

    result
  }


  //case class Aircraft(id:Long, name: String, emails: List[String], phones: List[String])
  //object Aircraft {
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
