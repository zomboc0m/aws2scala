package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import com.monsanto.arch.awsutil.StreamingAwsClient
import akka.stream.scaladsl.Flow
import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, GetFunctionRequest, LambdaFunction}

trait StreamingLambdaClient extends StreamingAwsClient {
  /** Returns a flow that creates a lambda function and emits the new function */
  def functionCreator: Flow[CreateFunctionRequest, LambdaFunction, NotUsed]

  /** Returns a flow that given a function name will delete the function. */
  def functionDeleter: Flow[String, String, NotUsed]

  /** Returns a flow that, given a request to retrieve a lambda function, emits the requested function */
  def functionGetter: Flow[GetFunctionRequest, LambdaFunction, NotUsed]
}
