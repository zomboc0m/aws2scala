package com.monsanto.arch.awsutil.converters

import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.lambda.model.{CodeLocation, FunctionArn, GetFunctionRequest, LambdaFunction,Runtime}

object LambdaConverters {
  implicit class AwsGetFunctionRequest(val request: aws.GetFunctionRequest) extends AnyVal {
    def asScala: GetFunctionRequest = GetFunctionRequest(request.getFunctionName)
  }

  implicit class ScalaGetFunctionRequest(val request: GetFunctionRequest) extends AnyVal {
    def asAws: aws.GetFunctionRequest = new aws.GetFunctionRequest().withFunctionName(request.name)
  }

  implicit class ScalaLambdaFunction(val result: aws.GetFunctionResult) extends AnyVal {
    def asScala: LambdaFunction = {
      def toRuntime(runtimeName: String): Runtime =
        Runtime.unapply(runtimeName).getOrElse(
          throw new IllegalArgumentException(s"Could not find Scala equivalent for $runtimeName"))

      def parseDate(timestamp: String) = {
        val lm = java.time.Instant.parse(timestamp)
        java.util.Date.from(lm)
      }

      val code = result.getCode
      val fc = result.getConfiguration

      new LambdaFunction(
        FunctionArn.fromArnString(fc.getFunctionArn),
        fc.getFunctionName,
        toRuntime(fc.getRuntime),
        fc.getHandler,
        fc.getRole,
        Some(fc.getDescription),
        Some(fc.getTimeout),
        Some(parseDate(fc.getLastModified)),
        Some(fc.getVersion),
        Some(fc.getCodeSha256),
        Some(CodeLocation(code.getLocation,code.getRepositoryType))
      )
    }
  }
}
