package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import com.monsanto.arch.awsutil.AsyncAwsClient
import com.monsanto.arch.awsutil.lambda.model.{FunctionArn, LambdaFunction}

import scala.concurrent.Future

trait AsyncLambdaClient extends  AsyncAwsClient {
  /** Retrieves information about a lambda function based on its name
    * */
  def getFunction(functionName: String)(implicit m: Materializer): Future[LambdaFunction]

  /** Retrieves information about a lambda function based on its ARN
    * */
  def getFunction(functionArn: FunctionArn)(implicit m: Materializer): Future[LambdaFunction]
}
