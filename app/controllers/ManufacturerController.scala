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

  val manufacturerForm = Form(
    mapping(
      "id" -> longNumber(min = 0),
      "description" -> text(minLength = 2, maxLength = 100),
      "link" -> optional(text(maxLength = 100)),
      "expiryDate" -> optional(date)
    )(Manufacturer.apply)(Manufacturer.unapply)
  )

  def save = Action { implicit request =>
    manufacturerForm.bindFromRequest.fold(
      formWithErrors => {
        // TODO melhorar tela de erro
        BadRequest(views.html.manufacturerEdit(formWithErrors))
      },
      manufacturer => {
        /* binding success, you get the actual value. */
        val newManufacturer = model.Manufacturer(manufacturer.id, manufacturer.description, manufacturer.link, manufacturer.expiryDate)
        val manufacturerCreate = dao.ManufacturerDao.merge(newManufacturer)
        Redirect(routes.ManufacturerController.edit(manufacturerCreate.id))
      }
    )
  }


  def validate(id: Long, description: String, link: Option[String], expiryDate: Option[Date]) = {
    Some(Manufacturer(id, description, link, expiryDate))
  }

  val manufacturerFormConstraintsAdHoc = Form(
    mapping(
      "id" -> longNumber,
      "description" -> text,
      "link" -> optional(text),
      "expiryDate" -> optional(date)
    )(Manufacturer.apply)(Manufacturer.unapply) verifying("Failed form constraints!", fields => fields match {
      case manufacturer => validate(manufacturer.id, manufacturer.description, manufacturer.link, manufacturer.expiryDate).isDefined
    })
  )


  def edit(id: Long) = Action {
    val manufacturerBanco = ManufacturerDao.getById(id);

    val manufacturer = manufacturerBanco.getOrElse(Manufacturer(0, "", None, None));

    val manufacturerData = Map("id" -> manufacturer.id.toString,
      "description" -> manufacturer.description,
      "link" -> manufacturer.link.getOrElse(""),
      "expiryDate" -> manufacturer.expiryDate.getOrElse(new Date()))

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

  def newRegister = Action {
    val manufacturer = Manufacturer(0, "", None, None);

    val manufacturerData = Map("id" -> manufacturer.id.toString, "description" -> manufacturer.description, "link" -> manufacturer.link.getOrElse(""))

    Ok(views.html.manufacturerEdit(manufacturerForm.bind(manufacturerData)))
  }

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
