package model

import java.util.Date

case class Manufacturer(var id: Long, description: String, link: Option[String], expiryDate: Option[Date])