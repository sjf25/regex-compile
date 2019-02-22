import scala.collection.immutable.HashSet
import scala.collection.immutable.NumericRange
import java.io.{PrintWriter, File}

object Runner {
  def main(args: Array[String]) {
    
    // email regex example
    val aToZ = Regex.mergeUlists(Regex.range('a', 'z'),
      Regex.range('A', 'Z'))
    val aToZNum = Regex.mergeUlists(Regex.range('0', '9'), aToZ)
    val part1 = Regex.oneOrMore(Regex.addToUlist(aToZNum, List('_', '.', '+', '-')))
    val part2 = Concat(Symbol('@'), Regex.oneOrMore(Regex.addToUlist(aToZNum, List('-'))))
    val part3 = Concat(Symbol('.'), Regex.oneOrMore(Regex.addToUlist(aToZNum, List('-', '.'))))
    val reg = Concat(part1, Concat(part2, part3))

    val alpha = reg.alphabet
    val dfa = reg.dfa(alpha)
    val (states, trans, accept, start) = reg.dfa(alpha)
    val cCode = new DFA(states, trans, accept, start).toC()
    println(cCode)
    /*
    println(s"$states\n$accept")
    println("TRANS:")
    trans.foreach(v => println(s"$v"))
    */
    /*
    if(args.length != 1) {
      println("incorrect usage")
      return
    }
    val regexStr = args(0)
    val parser = new ParseRegex()
    //val tmp = parser.parse(parser.test, regexStr)
    val tmp = parser.parse(parser.regex, regexStr)
    //val tmp = parser.parse(regexStr)
    //val tmp = ParseRegex.parseRegex(regexStr)
    println(s"$tmp")
    */
  }
}
