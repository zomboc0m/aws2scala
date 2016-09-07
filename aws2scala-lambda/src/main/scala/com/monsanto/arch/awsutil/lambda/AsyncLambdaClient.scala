package com.monsanto.arch.awsutil.lambda

import akka.Done
import akka.stream.Materializer
import com.monsanto.arch.awsutil.AsyncAwsClient
import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, FunctionArn, FunctionCode, LambdaFunction, Runtime}

import scala.concurrent.Future

trait AsyncLambdaClient extends AsyncAwsClient {
  /** Creates a new lambda function using only the required parameters
    *
    * @param pathToCode an absolute path to a .zip file containing the function code
    * @param name       the function name
    * @param handler    the function that Lambda will call when it is invoked
    * @param role       the arn of the role Lambda will assume
    * @param runtime    the runtime environment the function will use when invoked
    * */
  def createFunction(pathToCode: String, name: String, handler: String, role: String, runtime: Runtime)(implicit m: Materializer): Future[LambdaFunction]

  /** Creates a new lambda function using only the required parameters.  This version requires the user to specify the location of the code.  Use this version
    * if your code is in an S3 bucket
    *
    * @param code    the details of where to find the function code
    * @param name    the function name
    * @param handler the function that Lambda will call when it is invoked
    * @param role    the arn of the role Lambda will assume
    * @param runtime the runtime environment the function will use when invoked
    * */
  def createFunction(code: FunctionCode, name: String, handler: String, role: String, runtime: Runtime)(implicit m: Materializer): Future[LambdaFunction]

  /** Creates a new lambda function. Use this version to specify custom optional parameters.  See {@link com.monsanto.arch.awsutil.lambda.model.CreateFunctionRequest#apply} for what parameters are available
    *
    * @param request a request to aws to create a new lambda function
    * */
  def createFunction(request: CreateFunctionRequest)(implicit m: Materializer): Future[LambdaFunction]

  /** Deletes the function with the given name */
  def deleteFunction(name: String)(implicit m: Materializer): Future[Done]

  /** Retrieves information about a lambda function based on its name
    * */
  def getFunction(functionName: String)(implicit m: Materializer): Future[LambdaFunction]

  /** Retrieves information about a lambda function based on its ARN
    * */
  def getFunction(functionArn: FunctionArn)(implicit m: Materializer): Future[LambdaFunction]
}
