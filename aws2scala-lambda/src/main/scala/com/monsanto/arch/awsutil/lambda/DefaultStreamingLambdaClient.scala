package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.amazonaws.services.lambda.{AWSLambdaAsync, model ⇒ aws}

import com.monsanto.arch.awsutil.lambda.model.{GetFunctionRequest}
import com.monsanto.arch.awsutil.{AWSFlow}
import com.monsanto.arch.awsutil.converters.LambdaConverters._

/**
  * Created by stevenkohner on 8/16/16.
  */
class DefaultStreamingLambdaClient(lambda: AWSLambdaAsync) extends StreamingLambdaClient {
  override val functionGetter =
    Flow[GetFunctionRequest]
      .map(_.asAws)
      .via[aws.GetFunctionResult,NotUsed](AWSFlow.simple(lambda.getFunctionAsync))
      .map(_.asScala)
      .named("Lambda.functionGetter")
}
