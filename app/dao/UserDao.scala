package dao

import anorm.SqlParser._
import anorm._
import model.User
import play.api.Play.current
import play.api.db.DB


object UserDao {
  // TODO implementar o insert no banco
  def create(newUser: User):User = ???


  def rowMapper =  {
    long("id") ~
      str("user_name")~
      str("role") map {
      case id ~ name ~ role => User(id, name,role)
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
