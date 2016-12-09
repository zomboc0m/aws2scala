package com.monsanto.arch.awsutil.lambda.model

sealed abstract class Runtime(val name: String) {
  /** The string representation is the name. */
  override def toString: String = name
}

object Runtime {
  /** The older NodeJS v0.10.42 runtime */
  case object NodeJS extends Runtime("nodejs")

  /** The NodeJS v4.3 runtime */
  case object NodeJS43 extends Runtime("nodejs4.3")

  /** The NodeJS v4.3 edge runtime */
  case object NodeJS43Edge extends Runtime("nodejs4.3-edge")

  /** The Java 8 runtime */
  case object Java extends Runtime("java8")

  /** The Python 2.7 runtime */
  case object Python extends Runtime("python2.7")

  /** The DotNetCore 1.0 runtime */
  case object DotNetCore extends Runtime("dotnetcore1.0")

  /** All existing runtimes. */
  val values: Seq[Runtime] = Seq(NodeJS, NodeJS43, Java, Python, DotNetCore, NodeJS43Edge)

  /** Extractor for runtimes based on name. */
  def unapply(str: String): Option[Runtime] = Runtime.values.find(_.name == str)
}
