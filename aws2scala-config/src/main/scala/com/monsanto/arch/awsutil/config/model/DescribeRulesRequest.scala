package com.monsanto.arch.awsutil.config.model

/**
  * DescribeRulesRequest represents a request to AWS to retrieve one or more of the user's config rules
  *
  * @param names the names of the rules to return.  If blank, all rules will be returned
  */
case class DescribeRulesRequest(names: Option[Seq[String]])

object DescribeRulesRequest {
  /** Requests all roles. */
  val allRoles: DescribeRulesRequest = DescribeRulesRequest(None)
}
