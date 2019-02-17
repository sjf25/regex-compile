import scala.collection.immutable.NumericRange
//import scala.collection.mutable.HashSet
import scala.collection.immutable.HashSet
import scala.collection.mutable.{HashSet => MutHashSet}

sealed trait Regex {
  def delta: Regex = {
    this match {
      case Symbol(_) => Empty
      case Empty => Empty
      case Epsilon => Epsilon
      case Iterate(_) => Epsilon
      case Concat(a, b) => if (a.delta == Epsilon && b.delta == Epsilon) Epsilon else Empty
      case Complement(a) => if (a.delta == Epsilon) Empty else Epsilon
      case Union(a, b) => if (a.delta == Epsilon || b.delta == Epsilon) Epsilon else Empty
      case Intersection(a, b) => if (a.delta == Epsilon && b.delta == Epsilon) Epsilon else Empty
      case UnionList(_) => Empty
      //case UnionListWithEps(_) => Epsilon
    }
  }

  def unitDerivative(sym: Byte): Regex = {
    this match {
      case Symbol(other) => if (sym == other) Epsilon else Empty
      case Iterate(a) => Concat(a.unitDerivative(sym), this)
      case Concat(a, b) => Union(
        Concat(a.unitDerivative(sym), b),
        Concat(a.delta, b.unitDerivative(sym))
      )
      case Epsilon => Empty
      case Empty => Empty
      case Union(a, b) => Union(a.unitDerivative(sym), b.unitDerivative(sym))
      case Intersection(a, b) => Intersection(a.unitDerivative(sym), b.unitDerivative(sym))
      case Complement(a) => Complement(a.unitDerivative(sym))
      case UnionList(xs) => if (xs.contains(sym)) {
        Epsilon
        //Union(Epsilon, UnionList(xs - sym))
        //UnionListWithEps(xs - sym)
      } else {
        //this
        Empty
      }
      /*
      case UnionListWithEps(xs) => if (xs.contains(sym)) {
        UnionListWithEps(xs - sym)
      } else {
        this
      }
      */
    }
  }

  def derivative(seq: List[Byte]): Regex = {
    seq match {
      case x :: Nil => this.unitDerivative(x)
      case x:: xs => Concat(this.unitDerivative(x), this.derivative(xs))
      case Nil => this
    }
  }

  def simplifyPass: Regex = {
    this match {
      case Empty => Empty
      case Epsilon => Epsilon
      case Symbol(_) => this
      case Concat(_, Empty) => Empty
      case Concat(Empty, _) => Empty
      case Concat(a, Epsilon) => a.simplifyPass
      case Concat(Epsilon, a) => a.simplifyPass
      case Concat(a, b) => Concat(a.simplifyPass, b.simplifyPass)
      case Iterate(a) => Iterate(a.simplifyPass)
      case Complement(a) => Complement(a.simplifyPass)
      case Union(Empty, a) => a.simplifyPass
      case Union(a, Empty) => a.simplifyPass
      case Union(a, b) => Union(a.simplifyPass, b.simplifyPass)
      case Intersection(a, b) => Intersection(a.simplifyPass, b.simplifyPass)
      case UnionList(_) => this
      //case UnionListWithEps(_) => this
    }
  }

  def simplify: Regex = {
    val simp = this.simplifyPass
    if (simp.equals(this)) this else simp.simplify
  }

  def alphabet: Array[Boolean] = {
    var set = new Array[Boolean](256)
    this.alphabetHelper(set)
    set
  }

  def alphabetHelper(set: Array[Boolean]) {
    this match {
      case Symbol(x) => set(x) = true
      case Empty =>
      case Epsilon =>
      case Iterate(a) => a.alphabetHelper(set)
      case Complement(_) => ???
      case Concat(a, b) =>
        a.alphabetHelper(set)
        b.alphabetHelper(set)
      case Union(a, b) =>
        a.alphabetHelper(set)
        b.alphabetHelper(set)
      case Intersection(a, b) =>
        a.alphabetHelper(set)
        b.alphabetHelper(set)
      case UnionList(xs) => xs.map(x => set(x) = true)
      //case UnionListWithEps(xs) => xs.map(x => set(x) = true)
    }
  }
  def goto(alpha: Array[Boolean], q: Regex, c: Byte, states: MutHashSet[Regex],
    trans: MutHashSet[Tuple3[Regex, Byte, Regex]]) {
    val qc = q.unitDerivative(c).simplify
    if(states.contains(qc)) {
      trans.add((q, c, qc))
    }
    else {
      trans.add((q, c, qc))
      states.add(qc)
      explore(alpha, states, trans, qc)
    }
  }
  def explore(alpha: Array[Boolean], states: MutHashSet[Regex], trans: MutHashSet[Tuple3[Regex, Byte, Regex]], q: Regex) {
    q match  {
      /*
      case UnionList(xs) => 
        alpha.indices.foreach(i =>
            if(alpha(i)) {
              if(!alpha.contains(i))
                goto(alpha, q, i.toByte, states, trans)
              else
                trans.add((q, i.toByte, q))
            }
        )
        */
        /*
        xs.foreach(i => if(alpha(i)) goto(alpha, q, i.toByte, states, trans))
        if(xs.size != 256)
          trans.add((q, 
        */
      case _ => alpha.indices.foreach(i => if(alpha(i)) goto(alpha, q, i.toByte, states, trans))
    }
  }

  def acceptStates(states: MutHashSet[Regex]) : MutHashSet[Regex] = {
    var accepts: MutHashSet[Regex] = MutHashSet()
    states.foreach(q => if(q.delta == Epsilon) accepts.add(q))
    accepts
  }

  def dfa(alpha: Array[Boolean]): Tuple4[MutHashSet[Regex], MutHashSet[Tuple3[Regex, Byte, Regex]], MutHashSet[Regex], Regex] = {
    val simpExpr = this.simplify
    var states: MutHashSet[Regex] = MutHashSet()
    var trans: MutHashSet[Tuple3[Regex, Byte, Regex]] = MutHashSet()
    states.add(simpExpr)
    explore(alpha, states, trans, simpExpr)
    var accepts = acceptStates(states)
    (states, trans, accepts, simpExpr)
  }
}
case object Empty extends Regex
case object Epsilon extends Regex
case class Symbol(c: Byte) extends Regex
case class Iterate(r: Regex) extends Regex
case class Complement(r: Regex) extends Regex
case class Concat(fst: Regex, snd: Regex) extends Regex
case class Union(fst: Regex, snd: Regex) extends Regex
case class Intersection(fst: Regex, snd: Regex) extends Regex
case class UnionList(lst: HashSet[Byte]) extends Regex
//case class UnionListWithEps(lst: HashSet[Byte]) extends Regex

object Regex {
  def range(start: Byte, end: Byte): UnionList = {
    val lst = NumericRange.inclusive(start, end, 1: Byte).toList
    val set: HashSet[Byte] = HashSet(lst:_*)
    UnionList(set)
  }
  def mergeUlists(x: UnionList, y: UnionList): UnionList = {
    return UnionList(x.lst.union(y.lst))
  }
  def addToUlist(reg: UnionList, to_add: List[Byte]): UnionList = {
    UnionList(reg.lst ++ to_add)
  }
  def oneOrMore(r: Regex): Regex = {
    Concat(r, Iterate(r))
  }
  def repeatNTimes(r: Regex, n: Int): Regex = {
    var result: Regex = Epsilon
    for(i <- 1 to n) {
      result = Concat(r, result)
    }
    result
  }
}

/*
object Regex {
  def range(start: Byte, end: Byte): UnionList = {
    //UnionList(NumericRange(start, end, 1: Byte).toList)
    UnionList(NumericRange.inclusive(start, end, 1: Byte).toList)
  }
}
*/
