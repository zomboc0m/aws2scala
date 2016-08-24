package com.monsanto.arch.awsutil.lambda.model

import java.util.Date

/** LambdaFunction represents a lambda function.  It is a composite of the
  * Configuration and Code objects returned by the GetFunction function in aws
  *
  * @param arn the ARN of the lambda function
  * @param name the name of the lambda function
  * @param runtime the runtime used by the lambda function
  * @param handler the function in the code that will be called when the
  *                lambda function is invoked
  * @param role the role assumed by the lambda function when it is invoked
  * @param description an optional description of the function
  * @param timeout the maximum amount of time a lambda function will be allowed to run
  * @param lastModified the date and time that the function was last modified
  * @param version the version of the lambda function
  * @param codeHash the SHA256 hash of the function's code
  * @param codeLocation information about where a copy of the function's code can be found
  *
  * */
case class LambdaFunction(
                         arn: FunctionArn,
                         name: String,
                         runtime: Runtime,
                         handler: String,
                         role: String,
                         description: Option[String],
                         timeout: Option[Int],
                         lastModified: Option[Date],
                         version: Option[String],
                         codeHash: Option[String],
                         codeLocation: Option[CodeLocation])
