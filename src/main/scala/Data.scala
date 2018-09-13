case class UserInfo(id: String, name: String)

case class UserLocation(id: String, x: String)

case class CityInfo(name: String, x: String)

object Data {
  val users = Seq(
    UserInfo("1", "Alice"),
    UserInfo("2", "Bob"),
    UserInfo("3", "Charlie")
  )

  val cities = Seq(
    CityInfo("A", "0"),
//    CityInfo("B", "100"),
    CityInfo("C", "200"),
//    CityInfo("D", "300"),
//    CityInfo("E", "400"),
    CityInfo("F", "500")
  )
}
