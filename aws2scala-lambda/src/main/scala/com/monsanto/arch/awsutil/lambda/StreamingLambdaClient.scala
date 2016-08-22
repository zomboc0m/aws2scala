package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import com.monsanto.arch.awsutil.StreamingAwsClient
import akka.stream.scaladsl.Flow
import com.monsanto.arch.awsutil.lambda.model.{GetFunctionRequest, LambdaFunction}

trait StreamingLambdaClient extends  StreamingAwsClient {
  def functionGetter: Flow[GetFunctionRequest,LambdaFunction,NotUsed]
}
