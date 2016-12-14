package com.monsanto.arch.awsutil.lambda.model

/**
  * If a lambda function requires resources in a vpc, VpcConfig allows you to specify the function's
  * permissions withing that vpc.  Note: the security group and subnet ids must be from the same VPC
  * or an error will occur
  *
  * @param securityGroupIds A list of one or more security groups IDs in your VPC
  * @param subnetIds        A list of one or more subnet IDs in your VPC
  */
case class VpcConfig(securityGroupIds: Option[List[String]], subnetIds: Option[List[String]])


/**
  * A version of VpcConfig used in response objects
  *
  * @param securityGroupIds A list of one or more security groups IDs in your VPC
  * @param subnetIds        A list of one or more subnet IDs in your VPC
  */
case class VpcConfigResponse(securityGroupIds: Option[List[String]], subnetIds: Option[List[String]], vpcId: Option[String])
