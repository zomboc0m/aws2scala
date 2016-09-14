package com.monsanto.arch.awsutil.lambda.model

/** RemovePermissionRequest represents a request to remove permissions from a specified lambda function
  *
  * @param statementId  the statement id of the permission that will be removed
  * @param functionName the name of the lambda function from whom the permission is being removed
  *
  */
case class RemovePermissionRequest(statementId: String, functionName: String)
