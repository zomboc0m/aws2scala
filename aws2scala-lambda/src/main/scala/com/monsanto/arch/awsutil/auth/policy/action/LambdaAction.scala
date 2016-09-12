package com.monsanto.arch.awsutil.auth.policy.action

import com.monsanto.arch.awsutil.auth.policy.Action
import com.amazonaws.auth.policy

/** Type for all AWS access control policy actions for AWS Lambda. */
sealed abstract class LambdaAction(_name: String) extends Action(s"lambda:${_name}")


object LambdaAction {

  /** Represents any action executed on AWS Lambda. */
  case object AllLambdaActions extends LambdaAction("*")

  /** Action for the AddPermission operation. */
  case object AddPermission extends LambdaAction("AddPermission")

  /** Action for the CreateAlias operation. */
  case object CreateAlias extends LambdaAction("CreateAlias")

  /** Action for the CreateEventSourceMapping operation. */
  case object CreateEventSourceMapping extends LambdaAction("CreateEventSourceMapping")

  /** Action for the CreateFunction operation. */
  case object CreateFunction extends LambdaAction("CreateFunction")

  /** Action for the DeleteAlias operation. */
  case object DeleteAlias extends LambdaAction("DeleteAlias")

  /** Action for the DeleteEventSourceMapping operation. */
  case object DeleteEventSourceMapping extends LambdaAction("DeleteEventSourceMapping")

  /** Action for the DeleteFunction operation. */
  case object DeleteFunction extends LambdaAction("DeleteFunction")

  /** Action for the GetAlias operation. */
  case object GetAlias extends LambdaAction("GetAlias")

  /** Action for the GetEventSourceMapping operation. */
  case object GetEventSourceMapping extends LambdaAction("GetEventSourceMapping")

  /** Action for the GetFunction operation. */
  case object GetFunction extends LambdaAction("GetFunction")

  /** Action for the GetFunctionConfiguration operation. */
  case object GetFunctionConfiguration extends LambdaAction("GetFunctionConfiguration")

  /** Action for the GetPolicy operation. */
  case object GetPolicy extends LambdaAction("GetPolicy")

  /** Action for the Invoke operation. */
  case object Invoke extends LambdaAction("Invoke")

  /** Action for the InvokeAsync operation. */
  case object InvokeAsync extends LambdaAction("InvokeAsync")

  /** Action for the InvokeFunction operation */
  case object InvokeFunction extends LambdaAction("InvokeFunction")

  /** Action for the ListAliases operation. */
  case object ListAliases extends LambdaAction("ListAliases")

  /** Action for the ListEventSourceMappings operation. */
  case object ListEventSourceMappings extends LambdaAction("ListEventSourceMappings")

  /** Action for the ListFunctions operation. */
  case object ListFunctions extends LambdaAction("ListFunctions")

  /** Action for the ListVersionsByFunction operation. */
  case object ListVersionsByFunction extends LambdaAction("ListVersionsByFunction")

  /** Action for the PublishVersion operation. */
  case object PublishVersion extends LambdaAction("PublishVersion")

  /** Action for the RemovePermission operation. */
  case object RemovePermission extends LambdaAction("RemovePermission")

  /** Action for the UpdateAlias operation. */
  case object UpdateAlias extends LambdaAction("UpdateAlias")

  /** Action for the UpdateEventSourceMapping operation. */
  case object UpdateEventSourceMapping extends LambdaAction("UpdateEventSourceMapping")

  /** Action for the UpdateFunctionCode operation. */
  case object UpdateFunctionCode extends LambdaAction("UpdateFunctionCode")

  /** Action for the UpdateFunctionConfiguration operation. */
  case object UpdateFunctionConfiguration extends LambdaAction("UpdateFunctionConfiguration")

  val values: Seq[LambdaAction] = Seq(
    AllLambdaActions, AddPermission, CreateAlias, CreateEventSourceMapping, CreateFunction, DeleteAlias,
    DeleteEventSourceMapping, DeleteFunction, GetAlias, GetEventSourceMapping, GetFunction, GetFunctionConfiguration,
    GetPolicy, Invoke, InvokeAsync, InvokeFunction, ListAliases, ListEventSourceMappings, ListFunctions, ListVersionsByFunction,
    PublishVersion, RemovePermission, UpdateAlias, UpdateEventSourceMapping, UpdateFunctionCode, UpdateFunctionConfiguration
  )

  // Since the AWS SDK does not have actual lambda actions types, we can't use the normal register action approach
  // This is a very hacky solution, and action should probably be refactored to allow for this sort of operation
  // so that changes could be made to Action's implementation without breaking lambda
  private[awsutil] def registerActions(): Unit ={
    Action.nameToScalaConversion ++= values.map(a => (a.name,a))
  }

//  the aws sdk does not have these yet
//  private[awsutil] def registerActions(): Unit =
//    Action.registerActions(
//      LambdaActions.AllLambdaActions → AllLambdaActions,
//      LambdaActions.AddPermission → AddPermission,
//      LambdaActions.CreateAlias → CreateAlias,
//      LambdaActions.CreateEventSourceMapping → CreateEventSourceMapping,
//      LambdaActions.CreateFunction → CreateFunction,
//      LambdaActions.DeleteAlias → DeleteAlias,
//      LambdaActions.DeleteEventSourceMapping → DeleteEventSourceMapping,
//      LambdaActions.DeleteFunction → DeleteFunction,
//      LambdaActions.GetAlias → GetAlias,
//      LambdaActions.GetEventSourceMapping → GetEventSourceMapping,
//      LambdaActions.GetFunction → GetFunction,
//      LambdaActions.GetFunctionConfiguration → GetFunctionConfiguration,
//      LambdaActions.GetPolicy → GetPolicy,
//      LambdaActions.Invoke → Invoke,
//      LambdaActions.InvokeAsync → InvokeAsync,
//      LambdaActions.InvokeFunction → InvokeFunction,
//      LambdaActions.ListAliases → ListAliases,
//      LambdaActions.ListEventSourceMappings → ListEventSourceMappings,
//      LambdaActions.ListFunctions → ListFunctions,
//      LambdaActions.ListVersionsByFunction → ListVersionsByFunction,
//      LambdaActions.PublishVersion → PublishVersion,
//      LambdaActions.RemovePermission → RemovePermission,
//      LambdaActions.UpdateAlias → UpdateAlias,
//      LambdaActions.UpdateEventSourceMapping → UpdateEventSourceMapping,
//      LambdaActions.UpdateFunctionCode → UpdateFunctionCode,
//      LambdaActions.UpdateFunctionConfiguration → UpdateFunctionConfiguration
//    )

}
