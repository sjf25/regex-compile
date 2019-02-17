import scala.collection.immutable.HashSet
import scala.collection.immutable.NumericRange
import java.io.{PrintWriter, File}

object Runner {
  def main(args: Array[String]) {
    //println(Regex.range('a', 'c'))
    /*
    val reg = Union(
      Concat(Symbol('a'), Symbol('b')),
      Concat(Symbol('a'), Symbol('c')),
    )
    */
    //val reg = Regex.range('a', 'z')
    //val reg = Regex.range('a', 'c')
    //val reg = Union(Symbol('a'), Symbol('b'))

    
    /*
    val aToZ = Regex.mergeUlists(Regex.range('a', 'z'),
      Regex.range('A', 'Z'))
    val aToZNum = Regex.mergeUlists(Regex.range('0', '9'), aToZ)
    val part1 = Regex.oneOrMore(Regex.addToUlist(aToZNum, List('_', '.', '+', '-')))
    val part2 = Concat(Symbol('@'), Regex.oneOrMore(Regex.addToUlist(aToZNum, List('-'))))
    val part3 = Concat(Symbol('.'), Regex.oneOrMore(Regex.addToUlist(aToZNum, List('-', '.'))))
    val reg = Concat(part1, Concat(part2, part3))
    */

    for(n <- List(1, 2, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)) {
      val repeated = Regex.repeatNTimes(Symbol('a'), n)
      val reg = Concat(Union(Epsilon, repeated), repeated)

      val alpha = reg.alphabet

      val (states, trans, accept, start) = reg.dfa(alpha)
      //val cCode = DFA.toC(states, trans, accept, start)
      val cCode = new DFA(states, trans, accept, start).toC(s"test_$n")
      val writer = new PrintWriter(new File(s"../c_code/repeat_test_$n.h"))
      writer.write(cCode)
      writer.close()
    }

   /*
    val dfa = reg.dfa(alpha)
    val (states, trans, accept, _) = reg.dfa(alpha)
    println(s"$states\n$accept")
    println("TRANS:")
    trans.foreach(v => println(s"$v"))
    */
  }
}
