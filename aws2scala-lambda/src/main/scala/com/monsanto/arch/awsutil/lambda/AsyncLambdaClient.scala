package com.monsanto.arch.awsutil.lambda

import akka.Done
import akka.stream.Materializer
import com.monsanto.arch.awsutil.{Account, Arn, AsyncAwsClient}
import com.monsanto.arch.awsutil.auth.policy.{Action, Policy, Principal, Statement}
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
import com.monsanto.arch.awsutil.lambda.model._

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
  def createFunction(pathToCode: String, name: String, handler: String, role: RoleArn, runtime: Runtime)(implicit m: Materializer): Future[CreateFunctionResult]

  /** Creates a new lambda function using only the required parameters.  This version requires the user to specify the location of the code.  Use this version
    * if your code is in an S3 bucket
    *
    * @param code    the details of where to find the function code
    * @param name    the function name
    * @param handler the function that Lambda will call when it is invoked
    * @param role    the arn of the role Lambda will assume
    * @param runtime the runtime environment the function will use when invoked
    * */
  def createFunction(code: FunctionCode, name: String, handler: String, role: RoleArn, runtime: Runtime)(implicit m: Materializer): Future[CreateFunctionResult]

  /** Creates a new lambda function. Use this version to specify custom optional parameters.  See [[com.monsanto.arch.awsutil.lambda.model.CreateFunctionRequest#apply]] for what parameters are available
    *
    * @param request a request to aws to create a new lambda function
    * */
  def createFunction(request: CreateFunctionRequest)(implicit m: Materializer): Future[CreateFunctionResult]

  /** Deletes the function with the given name */
  def deleteFunction(name: String)(implicit m: Materializer): Future[Done]

  /** Retrieves information about a lambda function based on its name
    * */
  def getFunction(functionName: String)(implicit m: Materializer): Future[GetFunctionResult]

  /** Retrieves information about a lambda function based on its ARN
    * */
  def getFunction(functionArn: FunctionArn)(implicit m: Materializer): Future[GetFunctionResult]

  /** Adds a permission to a lambda function to allow an aws principal to use it
    *
    * @param statementId a unique identifier for this permission
    * @param functionName the name of a lambda function to attach the permission to.  Can be
    *                     the function name or arn
    * @param principal specifies who the access is granted to.  Can be an account, amazon service, etc.
    * @param action specifies what action will be granted to the principal
    * */
  def addPermission(statementId: String, functionName: String, principal: Principal, action: Action)(implicit m: Materializer): Future[Statement]

  /** Adds a permission to a lambda function to allow an aws principal to use it.  Use this method to specify more granular access,
    * restricting access to specific function by arn and account.  For example, to only allow a specific S3 bucket to invoke a
    * lambda function, specify Amazon S3 as the principal and use sourceArn and sourceAccount to specify the bucket
    *
    * @param statementId a unique identifier for this permission
    * @param functionName the name of a lambda function to attach the permission to.  Can be
    *                     the function name or arn
    * @param principal specifies who the access is granted to.  Can be an account, amazon service, etc.
    * @param action specifies what action will be granted to the principal
    * @param sourceArn specifies the arn of a specific resource to whom this permission will be granted
    * @param sourceAccount specifies a specific account to which the invoking resource must belong.  Usually, this sort of access
    *                      is covered when an account is specified as the principal. However, if a service, such as S3, is
    *                      the principal, this parameter can be used to further restrict permission
    * */
  def addPermission(statementId: String, functionName: String, principal: Principal, action: Action, sourceArn: Arn, sourceAccount: Account)(implicit m: Materializer): Future[Statement]

  /** Removes a permission from a lambda function
    *
    * @param statementId the id of the permission to remove
    * @param functionName the name of the function from which to remove the permission.  Can be the function name or ARN string
    * */
  def removePermission(statementId:String, functionName: String)(implicit m: Materializer): Future[Done]

  /** Retrieves the policy associated with a lambda function */
  def getPolicy(functionName: String)(implicit m: Materializer): Future[Policy]
}
