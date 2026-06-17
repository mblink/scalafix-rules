object Disable {
  val conn = new java.net.URI("http://localhost").toURL.openConnection

  val optionGet = Option(123).get
}
