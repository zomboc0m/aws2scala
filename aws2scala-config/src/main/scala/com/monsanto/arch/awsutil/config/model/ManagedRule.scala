package com.monsanto.arch.awsutil.config.model

/**
  * These rules represent the identifiers that correspond to the predefined rules managed by AWS
  */
sealed abstract class ManagedRule(val identifier: String) {
  override def toString = identifier
}

object ManagedRule {

  case object ApprovedAmisByID extends ManagedRule("APPROVED_AMIS_BY_ID")

  case object ApprovedAmisByTag extends ManagedRule("APPROVED_AMIS_BY_TAG")

  case object CloudTrailEnabled extends ManagedRule("CLOUD_TRAIL_ENABLED")

  case object DbInstanceBackupEnabled extends ManagedRule("DB_INSTANCE_BACKUP_ENABLED")

  case object DesiredInstanceTenancy extends ManagedRule("DESIRED_INSTANCE_TENANCY")

  case object DesiredInstanceType extends ManagedRule("DESIRED_INSTANCE_TYPE")

  case object DynamoDBThroughputLimitCheck extends ManagedRule("DYNAMODB_THROUGHPUT_LIMIT_CHECK")

  case object EbsOptimizedInstance extends ManagedRule("EBS_OPTIMIZED_INSTANCE")

  case object Ec2ManagedInstanceApplicationsBlacklisted extends ManagedRule("EC2_MANAGEDINSTANCE_APPLICATIONS_BLACKLISTED")

  case object Ec2ManagedInstanceApplicationsRequired extends ManagedRule("EC2_MANAGEDINSTANCE_APPLICATIONS_REQUIRED")

  case object Ec2ManagedInstancePlatformCheck extends ManagedRule("EC2_MANAGEDINSTANCE_PLATFORM_CHECK")

  case object EipAttached extends ManagedRule("EIP_ATTACHED")

  case object EncryptedVolumes extends ManagedRule("ENCRYPTED_VOLUMES")

  case object IamPasswordPolicy extends ManagedRule("IAM_PASSWORD_POLICY")

  case object IncomingSshDisabled extends ManagedRule("INCOMING_SSH_DISABLED")

  case object InstancesInVpc extends ManagedRule("INSTANCES_IN_VPC")

  case object RedshiftClusterConfigurationCheck extends ManagedRule("REDSHIFT_CLUSTER_CONFIGURATION_CHECK")

  case object RedshiftClusterMaintenanceSettingsCheck extends ManagedRule("REDSHIFT_CLUSTER_MAINTENANCESETTINGS_CHECK")

  case object RdsMultiAzSupport extends ManagedRule("RDS_MULTI_AZ_SUPPORT")

  case object RdsStorageEncrypted extends ManagedRule("RDS_STORAGE_ENCRYPTED")

  case object RequiredTags extends ManagedRule("REQUIRED_TAGS")

  case object RestrictedIncomingTraffic extends ManagedRule("RESTRICTED_INCOMING_TRAFFIC")

  case object RootAccountMfaEnabled extends ManagedRule("ROOT_ACCOUNT_MFA_ENABLED")

  case object S3BucketLoggingEnabled extends ManagedRule("S3_BUCKET_LOGGING_ENABLED")

  case object S3BucketVersioningEnabled extends ManagedRule("S3_BUCKET_VERSIONING_ENABLED")


  val values: Seq[ManagedRule] = Seq(
    ApprovedAmisByID, ApprovedAmisByTag, CloudTrailEnabled, DesiredInstanceTenancy, DbInstanceBackupEnabled,
    DesiredInstanceType, DynamoDBThroughputLimitCheck, EbsOptimizedInstance, Ec2ManagedInstanceApplicationsBlacklisted,
    Ec2ManagedInstanceApplicationsRequired, Ec2ManagedInstancePlatformCheck, EipAttached, EncryptedVolumes,
    IamPasswordPolicy, IncomingSshDisabled, InstancesInVpc, RedshiftClusterConfigurationCheck,
    RedshiftClusterMaintenanceSettingsCheck, RdsMultiAzSupport, RdsStorageEncrypted, RequiredTags,
    RestrictedIncomingTraffic, RootAccountMfaEnabled, S3BucketLoggingEnabled, S3BucketVersioningEnabled
  )

  def apply(str: String): ManagedRule =
    unapply(str).getOrElse(throw new IllegalArgumentException(s"‘$str’ is not a valid AWS Config managed rule"))

  def unapply(str: String): Option[ManagedRule] =
    values.find(_.identifier == str)
}
