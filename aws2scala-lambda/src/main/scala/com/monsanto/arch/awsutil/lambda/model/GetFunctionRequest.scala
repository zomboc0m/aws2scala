package com.monsanto.arch.awsutil.lambda.model

/** GetFunctionRequest represents a request to aws to retrieve information about a specified Lambda function
  *
  * @param name the name of the lambda function (the function's ARN works as well)
  * */
case class GetFunctionRequest(name: String)
