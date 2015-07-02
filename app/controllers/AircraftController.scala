package controllers

import dao.AircraftDao
import play.api._
import play.api.mvc._

import play.api.i18n.Messages.Implicits._
import play.api.i18n.Messages

import play.api.Play.current
import play.api.mvc._
import play.api.db._
import anorm.{SQL, SqlParser}
import anorm.SqlParser._
import anorm._


import model.Aircraft
import dao.AircraftDao
import play.api.mvc.Controller

import play.api.data._
import play.api.data.Forms._

//case class AircraftData(description: String, initials:String)

//http://marcelo-olivas.blogspot.com.br/2013/02/using-forms-with-play-framework-2-and.html
//https://scalaplayschool.wordpress.com/2014/08/17/lesson-7-scala-play-forms-bootstrap/

class AircraftController extends Controller {

  //val aircraft = aircraftForm.bindFromRequest.get

  val aircraftForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "description" -> text(minLength = 2, maxLength = 100),
      "initials" -> text(minLength = 2, maxLength = 40)
    )(Aircraft.apply)(Aircraft.unapply)
  )

  /*
  aircraftForm.bindFromRequest.fold(
    formWithErrors => {
      // TODO melhorar tela de erro
      // binding failure, you retrieve the form containing errors:
      //BadRequest(views.html.aircraft(formWithErrors))
      BadRequest("erro")
    },
    aircraft => {
      /* binding success, you get the actual value. */
      val newAircraft = model.Aircraft(aircraft.id, aircraft.description, aircraft.initials)
      val aircraftCreate = dao.AircraftDao.create(newAircraft)
      //Redirect(routes.AircraftController.edit(aircraftCreate.id))
      Redirect(routes.AircraftController.edit(aircraftCreate.id))
    }
  )*/


  def save = Action { implicit request =>
    aircraftForm.bindFromRequest.fold(
      formWithErrors => {
        // TODO melhorar tela de erro
        // binding failure, you retrieve the form containing errors:
        //BadRequest(views.html.aircraft(formWithErrors))

        /*Redirect(routes.AircraftController.newAircraft()).flashing(Flash(formWithErrors.data) +
          ("error" -> Messages("form.validation.error")))*/

        BadRequest(views.html.aircraftEdit(formWithErrors))

        //BadRequest(routes.AircraftController.edit(formWithErrors))
        //BadRequest("erro: "+formWithErrors.globalError)
        //BadRequest(views.html.index(formWithErrors))
      },
      aircraft => {
        /* binding success, you get the actual value. */
        val newAircraft = model.Aircraft(aircraft.id, aircraft.description, aircraft.initials)
        val aircraftCreate = dao.AircraftDao.merge(newAircraft)
        Redirect(routes.AircraftController.edit(aircraftCreate.id))
      }
    )
  }


  val aircraftFormConstraints2 = Form(
    mapping(
      "id" -> longNumber,
      "description" -> nonEmptyText,
      "initials" -> nonEmptyText
    )(Aircraft.apply)(Aircraft.unapply)
  )

  def validate(id: Long, description: String, initials: String) = {
    description match {
      case "root" if initials == "DBA" =>
        Some(Aircraft(id, description, initials))
      case "admin" =>
        Some(Aircraft(id, description, initials))
      case _ =>
        None
    }
  }

  val aircraftFormConstraintsAdHoc = Form(
    mapping(
      "id" -> longNumber,
      "description" -> text,
      "initials" -> text
    )(Aircraft.apply)(Aircraft.unapply) verifying("Failed form constraints!", fields => fields match {
      case aircraft => validate(aircraft.id, aircraft.description, aircraft.initials).isDefined
    })
  )

  /*
  val aircraftPost = Action(parse.form(aircraftForm)) { implicit request =>
    val aircraft = request.body
    val newAircraft = model.Aircraft(aircraft.id, aircraft.description, aircraft.initials)
    val aircraftCreate = AircraftDao.create(newAircraft)
    //Redirect(routes.Application.home(id))
    Redirect(routes.AircraftController.edit(aircraftCreate.id))
  }*/

  def edit(id: Long) = Action {
    val aircraftBanco = AircraftDao.getById(id);

    /*
    val result = aircraftBanco match {
      //case Some(u) => Ok(views.html.aircraftEdit(aircraftForm.bind(Map("id" -> u.id.toString, "description" -> u.description, "initials" -> u.initials))))
      case Some(u) => {
        val aircraftData1 = Map("id" -> u.id.toString, "description" -> u.description, "initials" -> u.initials)
        Ok(aircraftForm.bind(aircraftData1))
      }
      case _ => newAircraft()
    }*/

    /*
        return result;*/

    val aircraft = aircraftBanco.getOrElse(Aircraft(0, "", ""));

    val aircraftData = Map("id" -> aircraft.id.toString, "description" -> aircraft.description, "initials" -> aircraft.initials)

    Ok(views.html.aircraftEdit(aircraftForm.bind(aircraftData)))
  }

  def newAircraft = Action {
    val aircraft = Aircraft(0, "", "");

    val aircraftData = Map("id" -> aircraft.id.toString, "description" -> aircraft.description, "initials" -> aircraft.initials)

    Ok(views.html.aircraftEdit(aircraftForm.bind(aircraftData)))
  }

  // http://kev009.com/wp/2012/12/reusable-pactivator ui
  // gination-in-play-2/
  def aircraftList(page: Int) = Action {
    // TODO pegar da view
    val pageLength = 5
    val count = AircraftDao.getCount

    val offset = (page - 1) * pageLength
    val rowCount = pageLength
    val aircraftByPagination = AircraftDao.getByPagination(offset, rowCount)

    Ok(views.html.aircraftList(aircraftByPagination, count, page, pageLength))
  }



  def showAircraftByInitials = Action {
    var outString = "Aircraft: "


    outString += AircraftDao.getByInitials("dba")

    Ok(outString)

    //Ok(views.html.index("4"))
  }
}
