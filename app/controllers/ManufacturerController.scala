package controllers

import dao.ManufacturerDao
import model.Manufacturer
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Controller, _}

//case class ManufacturerData(description: String, initials:String)

//http://marcelo-olivas.blogspot.com.br/2013/02/using-forms-with-play-framework-2-and.html
//https://scalaplayschool.wordpress.com/2014/08/17/lesson-7-scala-play-forms-bootstrap/

class ManufacturerController extends Controller {

  //val manufacturer = manufacturerForm.bindFromRequest.get

  val manufacturerForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "description" -> text(minLength = 2, maxLength = 100),
      "initials" -> text(minLength = 2, maxLength = 40)
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
      val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.initials)
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
        val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.initials)
        val manufacturerCreate = dao.ManufacturerDao.merge(newManufacturer)
        Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
      }
    )
  }


  val manufacturerFormConstraints2 = Form(
    mapping(
      "id" -> longNumber,
      "description" -> nonEmptyText,
      "initials" -> nonEmptyText
    )(Manufacturer.apply)(Manufacturer.unapply)
  )

  def validate(id: Long, description: String, initials: String) = {
    description match {
      case "root" if initials == "DBA" =>
        Some(Manufacturer(id, description, initials))
      case "admin" =>
        Some(Manufacturer(id, description, initials))
      case _ =>
        None
    }
  }

  val manufacturerFormConstraintsAdHoc = Form(
    mapping(
      "id" -> longNumber,
      "description" -> text,
      "initials" -> text
    )(Manufacturer.apply)(Manufacturer.unapply) verifying("Failed form constraints!", fields => fields match {
      case manufacturer => validate(manufacturer.id, manufacturer.description, manufacturer.initials).isDefined
    })
  )

  /*
  val manufacturerPost = Action(parse.form(manufacturerForm)) { implicit request =>
    val manufacturer = request.body
    val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.initials)
    val manufacturerCreate = ManufacturerDao.create(newManufacturer)
    //Redirect(routes.Application.home(id))
    Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
  }*/

  def edit(id: Long) = Action {
    val manufacturerBanco = ManufacturerDao.getById(id);

    /*
    val result = manufacturerBanco match {
      //case Some(u) => Ok(views.html.manufacturerEdit(manufacturerForm.bind(Map("id" -> u.id.toString, "description" -> u.description, "initials" -> u.initials))))
      case Some(u) => {
        val manufacturerData1 = Map("id" -> u.id.toString, "description" -> u.description, "initials" -> u.initials)
        Ok(manufacturerForm.bind(manufacturerData1))
      }
      case _ => newManufacturer()
    }*/

    /*
        return result;*/

    val manufacturer = manufacturerBanco.getOrElse(Manufacturer(0, "", ""));

    val manufacturerData = Map("id" -> manufacturer.id.toString, "description" -> manufacturer.description, "initials" -> manufacturer.initials)

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

  def newManufacturer = Action {
    val manufacturer = Manufacturer(0, "", "");

    val manufacturerData = Map("id" -> manufacturer.id.toString, "description" -> manufacturer.description, "initials" -> manufacturer.initials)

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

  // http://kev009.com/wp/2012/12/reusable-pactivator ui
  // gination-in-play-2/
  def manufacturerList(page: Int) = Action {
    // TODO pegar da view
    val pageLength = 5
    val count = ManufacturerDao.getCount

    val offset = (page - 1) * pageLength
    val rowCount = pageLength
    val manufacturerByPagination = ManufacturerDao.getByPagination(offset, rowCount)

    Ok(views.html.manufacturerList(manufacturerByPagination, count, page, pageLength))
  }



  def showManufacturerByInitials = Action {
    var outString = "Manufacturer: "


    outString += ManufacturerDao.getByInitials("dba")

    Ok(outString)

    //Ok(views.html.index("4"))
  }
}
