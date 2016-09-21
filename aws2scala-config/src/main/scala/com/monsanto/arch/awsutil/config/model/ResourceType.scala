package com.monsanto.arch.awsutil.config.model

/**
  * A ResourceType represents the kinds of resources whose changes can trigger an
  * evaluation of the config rule
  */
sealed abstract class ResourceType(val service: String, val name: String) {
  override def toString = s"AWS::$service::$name"
}

object ResourceType {

  case object AWSACMCertificate extends ResourceType("ACM","Certificate")

  case object AWSCloudTrailTrail extends ResourceType("CloudTrail","Trail")

  case object AWSEC2CustomerGateway extends ResourceType("EC2","CustomerGateway")

  case object AWSEC2EIP extends ResourceType("EC2","EIP")

  case object AWSEC2Host extends ResourceType("EC2","Host")

  case object AWSEC2Instance extends ResourceType("EC2","Instance")

  case object AWSEC2InternetGateway extends ResourceType("EC2","InternetGateway")

  case object AWSEC2NetworkAcl extends ResourceType("EC2","NetworkAcl")

  case object AWSEC2NetworkInterface extends ResourceType("EC2","NetworkInterface")

  case object AWSEC2RouteTable extends ResourceType("EC2","RouteTable")

  case object AWSEC2SecurityGroup extends ResourceType("EC2","SecurityGroup")

  case object AWSEC2Subnet extends ResourceType("EC2","Subnet")

  case object AWSEC2Volume extends ResourceType("EC2","Volume")

  case object AWSEC2VPC extends ResourceType("EC2","VPC")

  case object AWSEC2VPNConnection extends ResourceType("EC2","VPNConnection")

  case object AWSEC2VPNGateway extends ResourceType("EC2","VPNGateway")

  case object AWSElasticLoadBalancingV2LoadBalancer extends ResourceType("ElasticLoadBalancingV2","LoadBalancer")

  case object AWSIAMGroup extends ResourceType("IAM","Group")

  case object AWSIAMPolicy extends ResourceType("IAM","Policy")

  case object AWSIAMRole extends ResourceType("IAM","Role")

  case object AWSIAMUser extends ResourceType("IAM","User")

  case object AWSRDSDBInstance extends ResourceType("RDS","DBInstance")

  case object AWSRDSDBSecurityGroup extends ResourceType("RDS","DBSecurityGroup")

  case object AWSRDSDBSnapshot extends ResourceType("RDS","DBSnapshot")

  case object AWSRDSDBSubnetGroup extends ResourceType("RDS","DBSubnetGroup")

  case object AWSRDSEventSubscription extends ResourceType("RDS","EventSubscription")

  val  values: Seq[ResourceType] = Seq(
    AWSACMCertificate, AWSCloudTrailTrail, AWSEC2CustomerGateway, AWSEC2EIP, AWSEC2Host,
    AWSEC2Instance, AWSEC2InternetGateway, AWSEC2NetworkAcl, AWSEC2NetworkInterface,
    AWSEC2RouteTable, AWSEC2SecurityGroup, AWSEC2Subnet, AWSEC2Volume, AWSEC2VPC,
    AWSEC2VPNConnection, AWSEC2VPNGateway, AWSElasticLoadBalancingV2LoadBalancer,
    AWSIAMGroup, AWSIAMPolicy, AWSIAMRole, AWSIAMUser, AWSRDSDBInstance, AWSRDSDBSecurityGroup,
    AWSRDSDBSnapshot, AWSRDSDBSubnetGroup, AWSRDSEventSubscription
  )
  def apply(str: String): ResourceType =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid resource type name."))

  def unapply(str: String): Option[ResourceType] =
    values.find(r => r.name == str || r.toString == str)

  val resourceTypeRegex = "AWS::\\w+::(\\w+)".r("name")

}
