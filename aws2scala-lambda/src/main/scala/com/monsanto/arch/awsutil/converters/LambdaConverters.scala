package com.monsanto.arch.awsutil.converters

import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.lambda.model.{CodeLocation, FunctionArn, GetFunctionRequest, LambdaFunction, Runtime}

/** Utility for converting ''aws2scala-lambda'' objects to/from their AWS counterparts. */
object LambdaConverters {
  implicit class AwsGetFunctionRequest(val request: aws.GetFunctionRequest) extends AnyVal {
    def asScala: GetFunctionRequest = GetFunctionRequest(request.getFunctionName)
  }

  implicit class ScalaGetFunctionRequest(val request: GetFunctionRequest) extends AnyVal {
    def asAws: aws.GetFunctionRequest = new aws.GetFunctionRequest().withFunctionName(request.name)
  }

  implicit class AwsGetFunctionResult(val result: aws.GetFunctionResult) extends AnyVal {
    def asScala: LambdaFunction = {
      def toRuntime(runtimeName: String): Runtime =
        Runtime.unapply(runtimeName).getOrElse(
          throw new IllegalArgumentException(s"Could not find Scala equivalent for $runtimeName"))

      val code = result.getCode
      val fc = result.getConfiguration

      new LambdaFunction(
        FunctionArn.fromArnString(fc.getFunctionArn),
        fc.getFunctionName,
        toRuntime(fc.getRuntime),
        fc.getHandler,
        fc.getRole,
        fc.getDescription,
        fc.getTimeout,
        fc.getLastModified,
        fc.getVersion,
        fc.getCodeSha256,
        CodeLocation(code.getLocation,code.getRepositoryType)
      )
    }
  }
  implicit class ScalaLambdaFunction(val function: LambdaFunction) extends AnyVal {
    def asAws: aws.GetFunctionResult = {
      val scalaCl = function.codeLocation
      val awsLocation = new aws.FunctionCodeLocation().withLocation(scalaCl.location).withRepositoryType(scalaCl.repositoryType)
      val awsConfig = new aws.FunctionConfiguration()
        .withFunctionArn(function.arn.arnString)
        .withFunctionName(function.name)
        .withRuntime(function.runtime.name)
        .withHandler(function.handler)
        .withRole(function.role)
        .withDescription(function.description)
        .withTimeout(function.timeout)
        .withLastModified(function.lastModified)
        .withCodeSha256(function.codeHash)
        .withVersion(function.version)

      new aws.GetFunctionResult()
        .withCode(awsLocation)
        .withConfiguration(awsConfig)
    }
  }
}
