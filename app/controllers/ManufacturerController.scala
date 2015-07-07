package controllers

import dao.ManufacturerDao
import model.Manufacturer
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Controller, _}

import java.util.Date

class ManufacturerController extends Controller {

  /*
  val manufacturerForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "description" -> text(minLength = 2, maxLength = 100),
      "link" -> optional(text(maxLength = 100)),
      "expiryDate" -> optional(date)
    )(Manufacturer.apply)(Manufacturer.unapply)
  )*/

  val manufacturerForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "description" -> text(minLength = 2, maxLength = 100),
      "link" -> text(maxLength = 100),
      "expiryDate" -> date
    )(Manufacturer.apply)(Manufacturer.unapply)
  )

  /*
  manufacturerForm.bindFromRequest.fold(
    formWithErrors => {
      // TODO melhorar tela de erro
      // binding failure, you retrieve the form containing errors:
      //BadRequest(views.html.manufacturer(formWithErrors))
      BadRequest("erro")
    },
    manufacturer => {
      /* binding success, you get the actual value. */
      val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.link)
      val manufacturerCreate = dao.ManufacturerDao.create(newManufacturer)
      //Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
      Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
    }
  )*/


  def save = Action { implicit request =>
    manufacturerForm.bindFromRequest.fold(
      formWithErrors => {
        // TODO melhorar tela de erro
        // binding failure, you retrieve the form containing errors:
        //BadRequest(views.html.manufacturer(formWithErrors))

        /*Redirect(routes.ManufacturerController.newManufacturer()).flashing(Flash(formWithErrors.data) +
          ("error" -> Messages("form.validation.error")))*/

        BadRequest(views.html.manufacturerEdit(formWithErrors))

        //BadRequest(routes.ManufacturerController.edit(formWithErrors))
        //BadRequest("erro: "+formWithErrors.globalError)
        //BadRequest(views.html.index(formWithErrors))
      },
      manufacturer => {
        /* binding success, you get the actual value. */
        val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.link, manufacturer.expiryDate)
        val manufacturerCreate = dao.ManufacturerDao.merge(newManufacturer)
        Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
      }
    )
  }


  val manufacturerFormConstraints2 = Form(
    mapping(
      "id" -> longNumber,
      "description" -> nonEmptyText,
      "link" -> nonEmptyText,
      "expiryDate" -> date
    )(Manufacturer.apply)(Manufacturer.unapply)
  )

  def validate(id: Long, description: String, link: String, expiryDate : Date) = {
    description match {
      case "root" if link == "DBA" =>
        Some(Manufacturer(id, description, link,expiryDate))
      case "admin" =>
        Some(Manufacturer(id, description, link,expiryDate))
      case _ =>
        None
    }
  }

  val manufacturerFormConstraintsAdHoc = Form(
    mapping(
      "id" -> longNumber,
      "description" -> text,
      "link" -> text,
      "expiryDate" -> date
    )(Manufacturer.apply)(Manufacturer.unapply) verifying("Failed form constraints!", fields => fields match {
      case manufacturer => validate(manufacturer.id, manufacturer.description, manufacturer.link, manufacturer.expiryDate).isDefined
    })
  )

  /*
  val manufacturerPost = Action(parse.form(manufacturerForm)) { implicit request =>
    val manufacturer = request.body
    val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.link)
    val manufacturerCreate = ManufacturerDao.create(newManufacturer)
    //Redirect(routes.Application.home(id))
    Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
  }*/

  def edit(id: Long) = Action {
    val manufacturerBanco = ManufacturerDao.getById(id);

    /*
    val result = manufacturerBanco match {
      //case Some(u) => Ok(views.html.manufacturerEdit(manufacturerForm.bind(Map("id" -> u.id.toString, "description" -> u.description, "link" -> u.link))))
      case Some(u) => {
        val manufacturerData1 = Map("id" -> u.id.toString, "description" -> u.description, "link" -> u.link)
        Ok(manufacturerForm.bind(manufacturerData1))
      }
      case _ => newManufacturer()
    }*/

    /*
        return result;*/

    val manufacturer = manufacturerBanco.getOrElse(Manufacturer(0, "", "", null));

    val manufacturerData = Map("id" -> manufacturer.id.toString, "description" -> manufacturer.description, "link" -> manufacturer.link)

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

  def newRegister = Action {
    val manufacturer = Manufacturer(0, "", "", null);

    val manufacturerData = Map("id" -> manufacturer.id.toString, "description" -> manufacturer.description, "link" -> manufacturer.link)

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

  // http://kev009.com/wp/2012/12/reusable-pactivator ui
  // gination-in-play-2/
  def list(page: Int) = Action {
    // TODO pegar da view
    val pageLength = 5
    val count = ManufacturerDao.getCount

    val offset = (page - 1) * pageLength
    val rowCount = pageLength
    val manufacturerByPagination = ManufacturerDao.getByPagination(offset, rowCount)

    Ok(views.html.manufacturerList(manufacturerByPagination, count, page, pageLength))
  }



}
