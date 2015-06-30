package controllers

import dao.UserDao
import play.api._
import play.api.mvc._

import play.api.i18n.Messages.Implicits._

import play.api.Play.current
import play.api.mvc._
import play.api.db._
import anorm.{SQL, SqlParser}
import anorm.SqlParser._
import anorm._


import model.User
import dao.UserDao
import play.api.mvc.Controller

import play.api.data._
import play.api.data.Forms._

//case class UserData(name: String, role:String)

//http://marcelo-olivas.blogspot.com.br/2013/02/using-forms-with-play-framework-2-and.html
//https://scalaplayschool.wordpress.com/2014/08/17/lesson-7-scala-play-forms-bootstrap/

class UserController extends Controller {

  //val user = userForm.bindFromRequest.get

  val userForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "name" -> text(minLength = 2, maxLength = 100),
      "role" -> text(minLength = 2, maxLength = 10)
    )(User.apply)(User.unapply)
  )

  /*
  userForm.bindFromRequest.fold(
    formWithErrors => {
      // TODO melhorar tela de erro
      // binding failure, you retrieve the form containing errors:
      //BadRequest(views.html.user(formWithErrors))
      BadRequest("erro")
    },
    user => {
      /* binding success, you get the actual value. */
      val newUser = model.User(user.id, user.name, user.role)
      val userCreate = dao.UserDao.create(newUser)
      //Redirect(routes.UserController.showUserById(userCreate.id))
      Redirect(routes.UserController.showUserById(userCreate.id))
    }
  )*/


  def save = Action { implicit request =>
    println("aaa")
    userForm.bindFromRequest.fold(
      formWithErrors => {
        // TODO melhorar tela de erro
        // binding failure, you retrieve the form containing errors:
        //BadRequest(views.html.user(formWithErrors))
        //BadRequest(views.html.userError(formWithErrors))
        BadRequest("erro")
      },
      user => {
        /* binding success, you get the actual value. */
        val newUser = model.User(user.id, user.name, user.role)
        val userCreate = dao.UserDao.merge(newUser)
        Redirect(routes.UserController.showUserById(userCreate.id))
      }
    )
  }


  val userFormConstraints2 = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "role" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

  def validate(id: Long, name: String, role: String) = {
    name match {
      case "root" if role == "DBA" =>
        Some(User(id, name, role))
      case "admin" =>
        Some(User(id, name, role))
      case _ =>
        None
    }
  }

  val userFormConstraintsAdHoc = Form(
    mapping(
      "id" -> longNumber,
      "name" -> text,
      "role" -> text
    )(User.apply)(User.unapply) verifying("Failed form constraints!", fields => fields match {
      case user => validate(user.id, user.name, user.role).isDefined
    })
  )

  /*
  val userPost = Action(parse.form(userForm)) { implicit request =>
    val user = request.body
    val newUser = model.User(user.id, user.name, user.role)
    val userCreate = UserDao.create(newUser)
    //Redirect(routes.Application.home(id))
    Redirect(routes.UserController.showUserById(userCreate.id))
  }*/

  def showUserById(id: Long) = Action {
    val userBanco = UserDao.getById(id);

    /*
    val result = userBanco match {
      //case Some(u) => Ok(views.html.userEdit(userForm.bind(Map("id" -> u.id.toString, "name" -> u.name, "role" -> u.role))))
      case Some(u) => {
        val userData1 = Map("id" -> u.id.toString, "name" -> u.name, "role" -> u.role)
        Ok(userForm.bind(userData1))
      }
      case _ => newUser()
    }*/

    /*
        return result;*/

    val user = userBanco.getOrElse(User(0, "", ""));

    val userData = Map("id" -> user.id.toString, "name" -> user.name, "role" -> user.role)

    Ok(views.html.userEdit(userForm.bind(userData)))
  }

  def newUser = Action {
    val user = User(0, "", "");

    val userData = Map("id" -> user.id.toString, "name" -> user.name, "role" -> user.role)

    Ok(views.html.userEdit(userForm.bind(userData)))
  }

  def showUserByRole = Action {
    var outString = "User: "


    outString += UserDao.getByRole("dba")

    Ok(outString)

    //Ok(views.html.index("4"))
  }
}
