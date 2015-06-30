package controllers

import dao.UserDao
import model.User
import play.api._
import play.api.mvc._


import play.api.Play.current
import play.api.mvc._
import play.api.db._
import anorm.{SQL, SqlParser}

import anorm.SqlParser._
import anorm._
import play.api.i18n.{Messages, MessagesApi, I18nSupport}

import javax.inject.Inject


class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {

    Messages("index.welcome")

    Ok(views.html.index())
  }

  def index2 = Action {
    Ok(views.html.index2("4"))
  }

  def dbTest = Action {
    var outString = "Number is "
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT 9 as testkey ")
      while (rs.next()) {
        outString += rs.getString("testkey")
      }
    } finally {
      conn.close()
    }
    Ok(outString)
  }

  def dbTest2 = Action {
    var outString = "Name: "
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT name,population from country ")
      while (rs.next()) {
        outString += rs.getString("name")
      }
    } finally {
      conn.close()
    }
    Ok(outString)
  }

}

