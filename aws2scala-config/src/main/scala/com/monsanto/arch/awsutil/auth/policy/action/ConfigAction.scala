package com.monsanto.arch.awsutil.auth.policy.action

import com.monsanto.arch.awsutil.auth.policy.Action

/** Type for all AWS Config policy actions. */
sealed abstract class ConfigAction(_name: String) extends Action(s"config:${_name}")

object ConfigAction {

  /** Represents any action executed on AWS Identity and Access Management. */
  case object AllConfigActions extends ConfigAction("*")

  /** Action for the DeleteConfigRule operation. */
  case object DeleteConfigRule extends ConfigAction("DeleteConfigRule")

  /** Action for the DeleteConfigurationRecorder operation. */
  case object DeleteConfigurationRecorder extends ConfigAction("DeleteConfigurationRecorder")

  /** Action for the DeleteDeliveryChannel operation. */
  case object DeleteDeliveryChannel extends ConfigAction("DeleteDeliveryChannel")

  /** Action for the DeleteEvaluationResults operation. */
  case object DeleteEvaluationResults extends ConfigAction("DeleteEvaluationResults")

  /** Action for the DeliverConfigSnapshot operation. */
  case object DeliverConfigSnapshot extends ConfigAction("DeliverConfigSnapshot")

  /** Action for the DescribeComplianceByConfigRule operation. */
  case object DescribeComplianceByConfigRule extends ConfigAction("DescribeComplianceByConfigRule")

  /** Action for the DescribeComplianceByResource operation. */
  case object DescribeComplianceByResource extends ConfigAction("DescribeComplianceByResource")

  /** Action for the DescribeConfigRuleEvaluationStatus operation. */
  case object DescribeConfigRuleEvaluationStatus extends ConfigAction("DescribeConfigRuleEvaluationStatus")

  /** Action for the DescribeConfigRules operation. */
  case object DescribeConfigRules extends ConfigAction("DescribeConfigRules")

  /** Action for the DescribeConfigurationRecorders operation. */
  case object DescribeConfigurationRecorders extends ConfigAction("DescribeConfigurationRecorders")

  /** Action for the DescribeConfigurationRecorderStatus operation. */
  case object DescribeConfigurationRecorderStatus extends ConfigAction("DescribeConfigurationRecorderStatus")

  /** Action for the DescribeDeliveryChannels operation. */
  case object DescribeDeliveryChannels extends ConfigAction("DescribeDeliveryChannels")

  /** Action for the DescribeDeliveryChannelStatus operation. */
  case object DescribeDeliveryChannelStatus extends ConfigAction("DescribeDeliveryChannelStatus")

  /** Action for the GetComplianceDetailsByConfigRule operation. */
  case object GetComplianceDetailsByConfigRule extends ConfigAction("GetComplianceDetailsByConfigRule")

  /** Action for the GetComplianceDetailsByResource operation. */
  case object GetComplianceDetailsByResource extends ConfigAction("GetComplianceDetailsByResource")

  /** Action for the GetComplianceSummaryByConfigRule operation. */
  case object GetComplianceSummaryByConfigRule extends ConfigAction("GetComplianceSummaryByConfigRule")

  /** Action for the GetComplianceSummaryByResourceType operation. */
  case object GetComplianceSummaryByResourceType extends ConfigAction("GetComplianceSummaryByResourceType")

  /** Action for the GetResourceConfigHistory operation. */
  case object GetResourceConfigHistory extends ConfigAction("GetResourceConfigHistory")

  /** Action for the ListDiscoveredResources operation. */
  case object ListDiscoveredResources extends ConfigAction("ListDiscoveredResources")

  /** Action for the PutConfigRule operation. */
  case object PutConfigRule extends ConfigAction("PutConfigRule")

  /** Action for the PutConfigurationRecorder operation. */
  case object PutConfigurationRecorder extends ConfigAction("PutConfigurationRecorder")

  /** Action for the PutDeliveryChannel operation. */
  case object PutDeliveryChannel extends ConfigAction("PutDeliveryChannel")

  /** Action for the PutEvaluations operation. */
  case object PutEvaluations extends ConfigAction("PutEvaluations")

  /** Action for the StartConfigRulesEvaluation operation. */
  case object StartConfigRulesEvaluation extends ConfigAction("StartConfigRulesEvaluation")

  /** Action for the StartConfigurationRecorder operation. */
  case object StartConfigurationRecorder extends ConfigAction("StartConfigurationRecorder")

  /** Action for the StopConfigurationRecorder operation. */
  case object StopConfigurationRecorder extends ConfigAction("StopConfigurationRecorder")

  val values: Seq[ConfigAction] = Seq(
    AllConfigActions, DeleteConfigRule, DeleteConfigurationRecorder, DeleteDeliveryChannel,
    DeleteEvaluationResults, DeliverConfigSnapshot, DescribeComplianceByConfigRule, DescribeComplianceByResource,
    DescribeConfigRuleEvaluationStatus, DescribeConfigRules, DescribeConfigurationRecorders,
    DescribeConfigurationRecorderStatus, DescribeDeliveryChannels, DescribeDeliveryChannelStatus,
    GetComplianceDetailsByConfigRule, GetComplianceDetailsByResource, GetComplianceSummaryByConfigRule,
    GetComplianceSummaryByResourceType, GetResourceConfigHistory, ListDiscoveredResources, PutConfigRule,
    PutConfigurationRecorder, PutDeliveryChannel, PutEvaluations, StartConfigRulesEvaluation,
    StartConfigurationRecorder, StopConfigurationRecorder
  )

  private[awsutil] def registerActions(): Unit ={
    Action.nameToScalaConversion ++= values.map(a => (a.name,a))
  }
}
