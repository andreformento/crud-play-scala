package controllers

import views.html

object MyHelpers {

  import views.html.helper.FieldConstructor

  implicit val myFields = FieldConstructor(html.myFieldConstructorTemplate.f)
}