package com.monsanto.arch.awsutil.lambda.model

import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.{Account, Arn}

/** FunctionArn represents the ARN of a Lambda Function
  *
  * @param region the region in which the function resides
  * @param account the account that owns the function
  * @param name the name of the function
  * */
case class FunctionArn(region: Region, account: Account, name: String) extends Arn(Arn.Namespace.Lambda, Some(region), account) {
  override def resource: String = s"function:$name"
}

object FunctionArn {
  /** Utility to build/extract `FunctionArn` instances from strings. */
  object fromArnString {
    /** Builds a `FunctionArn` object from the given ARN string. */
    def apply(arnString: String): FunctionArn =
      unapply(arnString).getOrElse(throw new IllegalArgumentException(s"‘$arnString’ is not a valid function ARN."))
    /** Extracts a `FunctionArn` object from the given ARN string. */
    def unapply(arnString: String): Option[FunctionArn] =
      arnString match {
        case Arn.fromArnString(accountArn: FunctionArn) ⇒ Some(accountArn)
        case _                                          ⇒ None
      }
  }

  /** This partial function will be registered with the Arn superclass so that it can extract/build instances of its subclasses */
  private[awsutil] val functionArnPF: PartialFunction[Arn.ArnParts, FunctionArn] = {
    case (_, Arn.Namespace.Lambda, Some(region), Some(account), FunctionResourceRegex(name)) ⇒
      FunctionArn(region, account, name)
  }

  private val FunctionResourceRegex = "^function:(.+)$".r
}
