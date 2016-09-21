package com.monsanto.arch.awsutil.config.model

/**
  * These rules represent the identifiers that correspond to the predefined rules managed by AWS
  */
sealed abstract class ManagedRule(val identifier: String) {
  override def toString = identifier
}

object ManagedRule {

  case object CloudTrailEnabled extends ManagedRule("CLOUD_TRAIL_ENABLED")

  case object DesiredInstanceTenancy extends ManagedRule("DESIRED_INSTANCE_TENANCY")

  case object EipAttached extends ManagedRule("EIP_ATTACHED")

  case object EncryptedVolumes extends ManagedRule("ENCRYPTED_VOLUMES")

  case object IncomingSshDisabled extends ManagedRule("INCOMING_SSH_DISABLED")

  case object InstancesInVpc extends ManagedRule("INSTANCES_IN_VPC")

  case object RequiredTags extends ManagedRule("REQUIRED_TAGS")

  case object RestrictedIncomingTraffic extends ManagedRule("RESTRICTED_INCOMING_TRAFFIC")

  case object RootAccountMfaEnabled extends ManagedRule("ROOT_ACCOUNT_MFA_ENABLED")


  val values: Seq[ManagedRule] = Seq(
    CloudTrailEnabled, DesiredInstanceTenancy, EipAttached, EncryptedVolumes, IncomingSshDisabled,
    InstancesInVpc, RequiredTags, RestrictedIncomingTraffic, RootAccountMfaEnabled
  )

  def apply(str: String): ManagedRule =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid AWS Config managed rule"))

  def unapply(str: String): Option[ManagedRule] =
    values.find(_.identifier == str)
}
