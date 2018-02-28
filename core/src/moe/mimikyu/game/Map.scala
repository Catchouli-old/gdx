package moe.mimikyu.game

class Map(layout: String) {
  val rows = layout.stripMargin.trim.split("\n").map(_.replace("\r", "")).map(_.map(decodeTile))

  private def decodeTile(c: Char) = {
    val charMap = Seq('A' to 'Z', 'a' to 'z', '0' to '9', Seq('+', '/')).flatten.zip(0 to 63).toMap
    charMap(c)
  }
}
