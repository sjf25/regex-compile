import scala.collection.mutable.{HashSet => MutHashSet, HashMap => MutHashMap}

class DFA(states: MutHashSet[Regex], trans: MutHashSet[Tuple3[Regex, Byte, Regex]], accepts: MutHashSet[Regex], start: Regex) {
  def toC(meth_name: String = "matches"): String = {
    //val (states, trans, accepts, start) = dfa
    val ids = stateIds
    val q0Id: Int = ids.get(start).get
    val acceptReturn = acceptSwitch(ids)
    val header = s"int $meth_name(char* str) {\nint state = $q0Id;\nwhile(*str){\n"
    val switch = outterSwitch(ids)
    val footer = s"\nstr++;\n}\n$acceptReturn}"
    header + switch + footer
  }
  def outterSwitch(ids: MutHashMap[Regex, Int]): String = {
    val inners = states.map(innerSwitch(_, ids))
    val innersJoined = inners.foldLeft(new StringBuilder){(sb, s) => sb.append(s)}.toString
    s"switch(state) {\n$innersJoined}"
  }
  def innerSwitch(state: Regex, ids: MutHashMap[Regex, Int]): String = {
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
  def acceptSwitch(ids: MutHashMap[Regex, Int]): String = {
    val acceptCases = accepts.map(state => {
      val stateId = ids.get(state).get
      s"case $stateId:\n"
    }).foldLeft(new StringBuilder){(sb, s) => sb.append(s)}.toString
    s"switch(state) {\n$acceptCases\nreturn 1;\ndefault: return 0;\n}\n"
  }
  def stateIds: MutHashMap[Regex, Int]  = {
    val ids: MutHashMap[Regex, Int] = MutHashMap()
    states.zipWithIndex.foreach(x => {
      val (y) = x
      val (s, i) = y
      ids.put(s, i)
    })
    ids
  }
  // TODO: use and test
  // uses modified version of floyd-warshall to check if path exists to accepts
  def earlyExitStates(ids: MutHashMap[Regex, Int]): MutHashSet[Regex] = {
    val edges: MutHashMap[Regex, Regex] = MutHashMap()
    val dist = Array.ofDim[Boolean](states.size, states.size)
    trans.foreach((t) => edges.put(t._1, t._3))
    edges.foreach((e) => {
      val fromId = ids.get(e._1).get
      val toId = ids.get(e._2).get
      dist(fromId)(toId) = true
    })
    states.foreach((s) => dist(ids.get(s).get)(ids.get(s).get) = true)
    for(k <- 0 to ids.size - 1) {
      for(i <- 0 to ids.size - 1) {
        for(j <- 0 to ids.size - 1) {
          if(!dist(i)(j) && dist(i)(k) && dist(k)(j)) {
            dist(i)(j) = true
          }
        }
      }
    }

    val earlyExits = states.filterNot((s) => {
      accepts.exists((acc) => dist(ids.get(s).get)(ids.get(acc).get))
    })
    earlyExits
  }
}
