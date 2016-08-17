package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import com.monsanto.arch.awsutil.AsyncAwsClient
import com.monsanto.arch.awsutil.lambda.model.{FunctionArn, LambdaFunction}

import scala.concurrent.Future

trait AsyncLambdaClient extends  AsyncAwsClient {
  def getFunction(functionName: String)(implicit m: Materializer): Future[LambdaFunction]

  def getFunction(functionArn: FunctionArn)(implicit m: Materializer): Future[LambdaFunction]
}
