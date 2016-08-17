package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import com.monsanto.arch.awsutil.AsyncAwsClient

import scala.concurrent.Future

trait AsyncLambdaClient extends  AsyncAwsClient {
  def getFunction(functionName: String)(implicit m: Materializer): Future[LambdaFunction]
}
