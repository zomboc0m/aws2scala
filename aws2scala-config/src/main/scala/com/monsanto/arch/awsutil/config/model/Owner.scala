package com.monsanto.arch.awsutil.config.model

/**
  * Specifies whether AWS or the customer owns and manages the config rule
  */
sealed abstract class Owner(val name: String) {
  override def toString = name
}

object Owner {

  case object AWS extends Owner("AWS")

  case object CustomLambda extends Owner("CUSTOM_LAMBDA")

  val values: Seq[Owner] = Seq(
    AWS, CustomLambda
  )

  def apply(str: String): Owner =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid class of owner for a config rule"))

  def unapply(str: String): Option[Owner] =
    values.find(_.name == str)
}
