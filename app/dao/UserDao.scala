package dao

import anorm.SqlParser._
import anorm._
import model.User
import play.api.Play.current
import play.api.db.DB


// passar para slick
// https://www.playframework.com/documentation/3.0.x/PlaySlick
// https://www.playframework.com/documentation/3.0.x/PlaySlickMigrationGuide
object UserDao {

  def merge(newUser: User): User = {
    if (newUser.id == 0)
      newUser.id = insert(newUser).getOrElse(0)
    else
      update(newUser)

    return newUser;
  }

  /**
   * This method returns the value of the auto_increment field when the stock is inserted
   * into the database table.
   * http://alvinalexander.com/scala/play-framework-anorm-insert-method-returns-auto-insert-id
   */
  def insert(newUser: User): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into user (user_name, role) values ({name}, {role})")
        .on("name" -> newUser.name,
          "role" -> newUser.role)
        .executeInsert()
    }
    id
  }

  def update(newUser: User) = {
    DB.withConnection { implicit c =>
      SQL( """
        update user set
          user_name={name},
          role={role}
        where id={id}
           """)
        .on("name" -> newUser.name,
          "role" -> newUser.role,
          "id" -> newUser.id)
        .execute()
    }
  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM user WHERE id = {id}")
        .on('id -> id)
        .executeUpdate()
      nRowsDeleted
    }
  }

  def rowMapper = {
    long("id") ~
      str("user_name") ~
      str("role") map {
      case id ~ name ~ role => User(id, name, role)
    }
  }

  def getById(id: Long): Option[User] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, user_name,role
              FROM user
             WHERE user.id = {id}
          """.stripMargin)
          .on("id" -> id)
          .as(UserDao.rowMapper singleOpt)
    }
  }

  def getByRole(role: String) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, user_name,role
              FROM user
             WHERE user.role = {role}
          """.stripMargin)
          .on("role" -> role)
          .as(UserDao.rowMapper *)
    }
  }

  def getByPagination(offset:Int, rowCount:Int) = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """
            SELECT id, user_name,role
              FROM user
            order by user_name
             limit {offset}, {rowCount}
          """.stripMargin)
          .on("offset" -> offset,
              "rowCount" -> rowCount)
          .as(UserDao.rowMapper *)
    }
  }

  def getCount: Int = {
    val result: Int =
      DB.withConnection { implicit c =>
        SQL("Select count(1) as c from User")
          .as(scalar[Int].single)
    }

    result
  }


  //case class User(id:Long, name: String, emails: List[String], phones: List[String])
  //object User {
  /* def rowMapper = {
     long("id") ~
       str("user_name") ~
       (str("email") ?) ~
       (str("number") ?) map {
       case id ~ name ~ email ~ number => ((id, name), email, number)
     }
   }*/
  //}

}
