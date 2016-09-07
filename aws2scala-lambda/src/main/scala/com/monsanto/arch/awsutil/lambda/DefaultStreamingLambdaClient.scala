package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.amazonaws.services.lambda.{AWSLambdaAsync, model => aws}
import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, GetFunctionRequest}
import com.monsanto.arch.awsutil.AWSFlow
import com.monsanto.arch.awsutil.converters.LambdaConverters._

class DefaultStreamingLambdaClient(lambda: AWSLambdaAsync) extends StreamingLambdaClient {
  override val functionCreator =
    Flow[CreateFunctionRequest]
      .map(_.asAws)
      .via[aws.CreateFunctionResult, NotUsed](AWSFlow.simple(lambda.createFunctionAsync))
      .map(_.asScala)
      .named("Lambda.functionCreator")

  override val functionGetter =
    Flow[GetFunctionRequest]
      .map(_.asAws)
      .via[aws.GetFunctionResult, NotUsed](AWSFlow.simple(lambda.getFunctionAsync))
      .map(_.asScala)
      .named("Lambda.functionGetter")
}
