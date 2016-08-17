package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.{Account, Arn}

case class FunctionArn(region: Region, account: Account, name: String) extends Arn(Arn.Namespace.Lambda, Some(region), account) {
  override def resource: String = s"function:$name"
}

object FunctionArn {
  object fromArnString {
    def apply(arnString: String): FunctionArn =
      unapply(arnString).getOrElse(throw new IllegalArgumentException(s"‘$arnString’ is not a valid function ARN."))

    def unapply(arnString: String): Option[FunctionArn] =
      arnString match {
        case Arn.fromArnString(accountArn: FunctionArn) ⇒ Some(accountArn)
        case _                                          ⇒ None
      }
  }

  private[awsutil] val functionArnPF: PartialFunction[Arn.ArnParts, FunctionArn] = {
    case (_, Arn.Namespace.Lambda, Some(region), Some(account), FunctionResourceRegex(name)) ⇒
      FunctionArn(region, account, name)
  }

  private val FunctionResourceRegex = "^function:(.+)$".r
}
