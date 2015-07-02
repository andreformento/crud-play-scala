package model

import org.joda.time.DateTime

case class Manufacturer(var id: Long, description: String, link: String, expiryDate: DateTime)