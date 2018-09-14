case class UserInfo(id: String, name: String)

case class UserLocation(id: String, x: String)

object Data {
  val users = Seq(
    UserInfo("1", "Alice"),
    UserInfo("2", "Bob"),
    UserInfo("3", "Charlie"),
    UserInfo("4", "unknown")
  )
}
