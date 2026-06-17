/*
rules = Disable
Disable.symbols = [
  {
    id = "javaURL"
    pattern = "^\\Qjava/net/URL#\\E.*$"
    message = "URL talks to the network for equality, prefer URI"
  }
  {
    id = "get"
    pattern = "^scala/(util/Try|Option|None|Some)#get\\(\\)\\.$"
    message = "this throws runtime exceptions, prefer methods that return an Option"
  }
]
*/

object Disable {
  val conn = new java.net.URI("http://localhost").toURL.openConnection/* assert: Disable.javaURL
                                                        ^^^^^^^^^^^^^^
  URL talks to the network for equality, prefer URI */

  val optionGet = Option(123).get/* assert: Disable.get
                              ^^^
  this throws runtime exceptions, prefer methods that return an Option */
}
