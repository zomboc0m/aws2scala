package com.monsanto.arch.awsutil.lambda.model

/** GetFunctionResult represents a response from AWS when it has been asked to describe a lambda function.  It is a composite of the
  * Configuration and Code objects returned by the GetFunction function in aws
  *
  * @param configuration the settings
  * @param codeLocation information about where a copy of the function's code can be found
  *
  **/
case class GetFunctionResult(
                              configuration: FunctionConfiguration,
                              codeLocation: CodeLocation)
