case class UserInfo(id: String, name: String)

case class UserLocation(id: String, x: String)

case class LocationInfo(name: String, x: String)

object Data {
  val users = Seq(
    UserInfo("1", "Alice"),
    UserInfo("2", "Bob"),
    UserInfo("3", "Charlie")
  )

  val locations = Seq(
    LocationInfo("A", "0"),
//    LocationInfo("B", "100"),
    LocationInfo("C", "200"),
//    LocationInfo("D", "300"),
//    LocationInfo("E", "400"),
    LocationInfo("F", "500")
  )
}
