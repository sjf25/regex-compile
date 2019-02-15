import scala.collection.mutable.{HashSet => MutHashSet, HashMap => MutHashMap}

object DFA {
  def toC(dfa: Tuple4[MutHashSet[Regex], MutHashSet[Tuple3[Regex, Byte, Regex]], MutHashSet[Regex], Regex]): String = {
    val (states, trans, accepts, start) = dfa
    val ids = stateIds(states)
    val q0Id: Int = ids.get(start).get
    val acceptReturn = acceptSwitch(accepts, ids)
    val header = s"int matches(char* str) {\nint state = $q0Id;\nwhile(*str){\n"
    val switch = outterSwitch(states, trans, ids)
    val footer = s"\nstr++;\n}\n$acceptReturn}"
    header + switch + footer
  }
  def outterSwitch(states: MutHashSet[Regex], trans: MutHashSet[Tuple3[Regex, Byte, Regex]], ids: MutHashMap[Regex, Int]): String = {
    val inners = states.map(innerSwitch(_, trans, ids))
    val innersJoined = inners.foldLeft(new StringBuilder){(sb, s) => sb.append(s)}.toString
    s"switch(state) {\n$innersJoined}"
  }
  def innerSwitch(state: Regex, trans: MutHashSet[Tuple3[Regex, Byte, Regex]], ids: MutHashMap[Regex, Int]): String = {
    val cases = trans.filter((t) => t._1.equals(state)).map(makeCase(_, ids))
    val casesJoined = cases.foldLeft(new StringBuilder){(sb, s) => sb.append(s)}.toString
    val stateId = ids.get(state).get
    s"case $stateId:\nswitch(*str) {\n$casesJoined\ndefault: return 0;\n}break;\n"
  }
  def makeCase(trans_tup: Tuple3[Regex, Byte, Regex], ids: MutHashMap[Regex, Int]): String = {
    val (src, sym, dest) = trans_tup
    val destId = ids.get(dest).get
    s"case $sym: state=$destId; break;\n"
  }
  def acceptSwitch(accepts: MutHashSet[Regex], ids: MutHashMap[Regex, Int]): String = {
    val acceptCases = accepts.map(state => {
      val stateId = ids.get(state).get
      s"case $stateId:\n"
    }).foldLeft(new StringBuilder){(sb, s) => sb.append(s)}.toString
    s"switch(state) {\n$acceptCases\nreturn 1;\ndefault: return 0;\n}\n"
  }
  def stateIds(states: MutHashSet[Regex]): MutHashMap[Regex, Int]  = {
    val ids: MutHashMap[Regex, Int] = MutHashMap()
    states.zipWithIndex.foreach(x => {
      val (y) = x
      val (s, i) = y
      ids.put(s, i)
    })
    ids
  }
}
