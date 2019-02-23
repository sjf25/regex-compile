import scala.util.parsing.combinator._
import scala.collection.immutable.HashSet

object ParseRegex extends RegexParsers with PackratParsers{
  def parse(s: String): ParseResult[Regex] = {
    parseAll(regex, s)
  }
  val regex: PackratParser[Regex] = charSet | iterate | oneOrMore | oneOrNone | concat | union | parenthesis | symbol
  val symbolByte = regex("""[^[\\^$.|?*+()]]""".r) ^^ { (str) =>
    assert(str.length == 1)
    str.charAt(0).toByte
  }
  val symbol = symbolByte ^^ { Symbol(_) }
  val union = regex ~ ("|" ~> regex) ^^ { case a ~ b => Union(a, b) }
  val iterate = regex <~ "*" ^^ { Iterate(_) }
  val concat = regex ~ regex ^^ { case a ~ b => Concat(a, b) }

  val oneOrMore = regex <~ "+" ^^ { Regex.oneOrMore(_) }
  val oneOrNone = regex <~ "?" ^^ { x => Union(Iterate(x), Epsilon) }
  val parenthesis = "(" ~> regex <~ ")"
  val range = """.""".r ~ ("-" ~> """.""".r) ^^ {
    case a ~ b => Regex.range(a.toByte, b.toByte)
  }
  val charSetElem = (range | (symbolByte ^^ Regex.ulistSingle))
  val charSet = "[" ~> charSetElem.+ <~ "]" ^^ { (xs) =>
    xs.fold(UnionList(HashSet()))(Regex.mergeUlists(_, _))
  }
}
