package com.monsanto.arch.awsutil.converters

import collection.JavaConverters._
import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.lambda.model._

/** Utility for converting ''aws2scala-lambda'' objects to/from their AWS counterparts. */
object LambdaConverters {
  def toRuntime(runtimeName: String): Runtime =
    Runtime.unapply(runtimeName).getOrElse(
      throw new IllegalArgumentException(s"Could not find Scala equivalent for $runtimeName"))

  implicit class AwsGetFunctionRequest(val request: aws.GetFunctionRequest) extends AnyVal {
    def asScala: GetFunctionRequest = GetFunctionRequest(request.getFunctionName)
  }

  implicit class ScalaGetFunctionRequest(val request: GetFunctionRequest) extends AnyVal {
    def asAws: aws.GetFunctionRequest = new aws.GetFunctionRequest().withFunctionName(request.name)
  }

  implicit class AwsCreateFunctionRequest(val request: aws.CreateFunctionRequest) extends AnyVal {
    def asScala: CreateFunctionRequest =
      CreateFunctionRequest(
        request.getCode.asScala,
        request.getFunctionName,
        request.getHandler,
        toRuntime(request.getRuntime),
        request.getRole,
        request.getDescription,
        request.getMemorySize,
        request.getPublish,
        request.getTimeout,
        Option(request.getVpcConfig).map(vpc => VpcConfig(vpc.getSecurityGroupIds.asScala.toList, vpc.getSubnetIds.asScala.toList))
      )
  }

  implicit class ScalaCreateFunctionRequest(val request: CreateFunctionRequest) extends AnyVal {
    def asAws: aws.CreateFunctionRequest =
      new aws.CreateFunctionRequest()
        .withCode(request.code.asAws)
        .withDescription(request.description)
        .withFunctionName(request.name)
        .withHandler(request.handler)
        .withMemorySize(request.memory)
        .withPublish(request.publish)
        .withRole(request.role)
        .withRuntime(request.runtime.name)
        .withTimeout(request.timeout)
        .withVpcConfig(request.vpcConfig.map { v =>
          new aws.VpcConfig()
            .withSecurityGroupIds(v.securityGroupIds: _*)
            .withSubnetIds(v.subnetIds: _*)
        }.orNull)
  }

  implicit class ScalaFunctionCode(val code: FunctionCode) extends AnyVal {
    def asAws: aws.FunctionCode = {
      val awsCode = new aws.FunctionCode()
      code.ZipFile.foreach(awsCode.withZipFile)
      code.S3Bucket.foreach(awsCode.withS3Bucket)
      code.S3Key.foreach(awsCode.withS3Key)
      code.S3ObjectVersion.foreach(awsCode.withS3ObjectVersion)
      awsCode
    }
  }

  implicit class AwsFunctionCode(val code: aws.FunctionCode) extends AnyVal {
    def asScala: FunctionCode =
      FunctionCode(
        Some(code.getS3Bucket),
        Some(code.getS3Key),
        Some(code.getS3ObjectVersion),
        Some(code.getZipFile)
      )
  }

  implicit class AwsGetFunctionResult(val result: aws.GetFunctionResult) extends AnyVal {
    def asScala: LambdaFunction = {
      val code = result.getCode
      val fc = result.getConfiguration

      LambdaFunction(
        FunctionArn.fromArnString(fc.getFunctionArn),
        fc.getFunctionName,
        toRuntime(fc.getRuntime),
        fc.getHandler,
        fc.getRole,
        fc.getDescription,
        fc.getTimeout,
        fc.getLastModified,
        fc.getMemorySize,
        fc.getVersion,
        fc.getCodeSha256,
        Option(fc.getVpcConfig).map(vpc => VpcConfig(vpc.getSecurityGroupIds.asScala.toList, vpc.getSubnetIds.asScala.toList)),
        Some(CodeLocation(code.getLocation, code.getRepositoryType))
      )
    }
  }

  implicit class AwsCreateFunctionResult(val result: aws.CreateFunctionResult) extends AnyVal {
    def asScala: LambdaFunction = {
      LambdaFunction(
        FunctionArn.fromArnString(result.getFunctionArn),
        result.getFunctionName,
        toRuntime(result.getRuntime),
        result.getHandler,
        result.getRole,
        result.getDescription,
        result.getTimeout,
        result.getLastModified,
        result.getMemorySize,
        result.getVersion,
        result.getCodeSha256,
        Option(result.getVpcConfig).map(vpc => VpcConfig(vpc.getSecurityGroupIds.asScala.toList, vpc.getSubnetIds.asScala.toList)),
        None)
    }
  }

  implicit class ScalaLambdaFunction(val function: LambdaFunction) extends AnyVal {
    def asAws: aws.GetFunctionResult = {
      val scalaCl = function.codeLocation.getOrElse(CodeLocation("", ""))
      val awsLocation = new aws.FunctionCodeLocation().withLocation(scalaCl.location).withRepositoryType(scalaCl.repositoryType)
      val awsConfig = new aws.FunctionConfiguration()
        .withFunctionArn(function.arn.arnString)
        .withFunctionName(function.name)
        .withRuntime(function.runtime.name)
        .withHandler(function.handler)
        .withRole(function.role)
        .withDescription(function.description)
        .withTimeout(function.timeout)
        .withMemorySize(function.memory)
        .withLastModified(function.lastModified)
        .withCodeSha256(function.codeHash)
        .withVersion(function.version)
        .withVpcConfig(function.vpcConfig.map { v =>
          new aws.VpcConfigResponse()
            .withSecurityGroupIds(v.securityGroupIds: _*)
            .withSubnetIds(v.subnetIds: _*)
        }.orNull)

      new aws.GetFunctionResult()
        .withCode(awsLocation)
        .withConfiguration(awsConfig)
    }
  }

  implicit class ScalaAddPermissionRequest(val request: AddPermissionRequest) extends AnyVal {
    def asAws: aws.AddPermissionRequest = {
      new aws.AddPermissionRequest()
        .withAction(request.action.name)
        .withFunctionName(request.functionName)
        .withPrincipal(request.principal.id)
        .withSourceAccount(request.sourceAccount.orNull)
        .withSourceArn(request.sourceArn.orNull)
        .withStatementId(request.statementId)
    }
  }

  implicit class ScalaRemovePermissionRequest(val requset: RemovePermissionRequest) extends AnyVal {
    def asAws: aws.RemovePermissionRequest = {
      new aws.RemovePermissionRequest()
        .withFunctionName(requset.functionName)
        .withStatementId(requset.statementId)
    }
  }

}
