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
    CityInfo("Kaohsiung", "-200.0"),
//    CityInfo("Tainan", "-100.0"),
    CityInfo("Taichung", "0.0"),
//    CityInfo("Hsinchu", "100.0"),
    CityInfo("Taipei", "200.0")
  )
}
