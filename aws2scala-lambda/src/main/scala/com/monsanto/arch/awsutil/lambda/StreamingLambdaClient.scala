package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import com.monsanto.arch.awsutil.StreamingAwsClient
import akka.stream.scaladsl.Flow
import com.monsanto.arch.awsutil.auth.policy.{Policy, Statement}
import com.monsanto.arch.awsutil.lambda.model._

trait StreamingLambdaClient extends StreamingAwsClient {
  /** Returns a flow that creates a lambda function and emits the new function */
  def functionCreator: Flow[CreateFunctionRequest, LambdaFunction, NotUsed]

  /** Returns a flow that given a function name will delete the function. */
  def functionDeleter: Flow[String, String, NotUsed]

  /** Returns a flow that, given a request to retrieve a lambda function, emits the requested function */
  def functionGetter: Flow[GetFunctionRequest, LambdaFunction, NotUsed]

  /** Returns a flow that adds a permission to an existing lambda function and emits the newly attached statement */
  def permissionAdder: Flow[AddPermissionRequest, Statement, NotUsed]

  /** Returns a flow that removes an existing permission from a lambda function and emits the id of the permission */
  def permissionRemover: Flow[RemovePermissionRequest, String, NotUsed]

  /** Returns a flow that retrieves the policy associated with a specified lambda function and emits the policy */
  def policyGetter: Flow[String, Policy, NotUsed]
}
