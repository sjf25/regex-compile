import scala.util.parsing.combinator._
//import com.codecommit.gll._
import scala.collection.immutable.HashSet
//import fastparse._, NoWhitespace._

/*
class ParseRegex extends RegexParsers {
  def symbolByte: Parser[Byte] = """[^[\\^$.|?*+()]]""".r ^^ { (str) =>
    assert(str.length == 1)
    str.charAt(0).toByte
  }
  def symbol: Parser[Regex] = symbolByte ^^ { Symbol(_) }
  def concat: Parser[Regex] = regex ~ regex ^^ { case a ~ b => Concat(a, b) }
  //def concat: Parser[Regex] = regex ~ regex ^^ { case (a, b) => Concat(a, b) }
  def iterate: Parser[Regex] = regex <~ "*" ^^ { Iterate(_) }
  def oneOrMore: Parser[Regex] = regex <~ "+" ^^ { Regex.oneOrMore(_) }
  def oneOrNone: Parser[Regex] = regex <~ "?" ^^ { x => Union(Iterate(x), Epsilon) }
  def union: Parser[Regex] =  regex ~ ("|" ~> regex) ^^ { case a ~ b => Union(a, b) }
  //def union: Parser[Regex] =  regex ~ ("|" ~> regex) ^^ { case (a, b) => Union(a, b) }
  def parenthesis: Parser[Regex] = "(" ~> regex <~ ")"
  def range: Parser[UnionList] = """.""".r ~ ("-" ~> """.""".r) ^^ {
    case a ~ b => Regex.range(a.toByte, b.toByte)
    //case (a, b) => Regex.range(a.toByte, b.toByte)
  }
  def charSetElem: Parser[UnionList] = (range | (symbolByte ^^ Regex.ulistSingle))
  def charSet: Parser[Regex] = "[" ~> charSetElem.+ <~ "]" ^^ { (xs) =>
    xs.fold(UnionList(HashSet()))(Regex.mergeUlists(_, _))
  }
  def regex: Parser[Regex] = parenthesis | charSet | iterate | oneOrMore | oneOrNone | union | concat | symbol

}
*/

class ParseRegex extends RegexParsers {
  /*
  def test: Parser[Regex] = (symbol ~ testConcat ^^ {case a ~ b => Concat(a, b)}) | symbol
  def testConcat: Parser[Regex] = (symbol ~ testConcat).? ^^ { _ match {
    case Some(a ~ b) => Concat(a, b)
    case None => Epsilon
  }}
  */
  def symbolByte: Parser[Byte] = """[^[\\^$.|?*+()]]""".r ^^ { (str) =>
    assert(str.length == 1)
    str.charAt(0).toByte
  }
  def symbol: Parser[Regex] = symbolByte ^^ { Symbol(_) }
  //def concat: Parser[Regex] = (concat | symbol) ~ regex ^^ { case a ~ b => Concat(a, b) }
  //def regex: Parser[Regex] = symbol ~ regexPrime ^^ { case a ~ b => Concat(a, b).simplify }
  def regex: Parser[Regex] = symbol ~ regexPrime ^^ { case a ~ b => Concat(a, b).simplify }
  def regexPrime: Parser[Regex] = (concat | union).? ^^ { _ match {
    case Some(r) => r
    case None => Epsilon
  }}
  def concat: Parser[Regex] = regex ~ regexPrime ^^ { case a ~ b => Concat(a, b) }
  def union: Parser[Regex] = "|" ~> regex ~ regexPrime ^^ { case a ~ b => Union(a, b) }
}

/*
object ParseRegex {
  val special = List('\\', '^', '$', '.', '|', '?', '*', '+', '(', ')', '[', ']')
  def symbolByte[_: P]: P[Byte] = P(CharPred(!special.contains(_)).rep.!.map(_.charAt(0).toByte))
  def symbol[_: P]: P[Regex] = P(symbolByte).map(Symbol(_))
  def concat[_: P]: P[Regex] = P(regex ~ regex).map({case (a, b) => Concat(a, b)})
  def regex[_: P]: P[Regex] = P(concat | symbol)
  def parseRegex(str: String) = parse(str, regex(_))
}
*/
