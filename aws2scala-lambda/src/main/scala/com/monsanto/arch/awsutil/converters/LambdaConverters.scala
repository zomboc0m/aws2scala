package com.monsanto.arch.awsutil.converters

import collection.JavaConverters._
import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
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
        RoleArn.fromArnString(request.getRole),
        Option(request.getDescription),
        Option(request.getMemorySize.intValue),
        Option(request.getPublish.booleanValue),
        Option(request.getTimeout.intValue),
        Option(request.getVpcConfig.asScala)
      )
  }

  implicit class ScalaCreateFunctionRequest(val request: CreateFunctionRequest) extends AnyVal {
    def asAws: aws.CreateFunctionRequest = {
      val cfr = new aws.CreateFunctionRequest()
        .withCode(request.code.asAws)
        .withDescription(request.description.orNull)
        .withFunctionName(request.name)
        .withHandler(request.handler)
        .withRole(request.role.arnString)
        .withRuntime(request.runtime.name)
        .withVpcConfig(request.vpcConfig.map(_.asAws).orNull)
      request.memory.foreach(cfr.withMemorySize(_))
      request.publish.foreach(cfr.withPublish(_))
      request.timeout.foreach(cfr.withTimeout(_))

      cfr
    }

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
    def asScala: GetFunctionResult = {
      val code = result.getCode
      val fc = result.getConfiguration

      GetFunctionResult(
        result.getConfiguration.asScala,
        result.getCode.asScala
      )
    }
  }

  implicit class ScalaGetFunctionResult(val result: GetFunctionResult) extends AnyVal {
    def asAws: aws.GetFunctionResult = {
      new aws.GetFunctionResult()
        .withCode(result.codeLocation.asAws)
        .withConfiguration(result.configuration.asAws)
    }
  }

  implicit class AwsCreateFunctionResult(val result: aws.CreateFunctionResult) extends AnyVal {
    def asScala: CreateFunctionResult = {
      CreateFunctionResult(
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
        result.getVpcConfig.asScala
      )
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

  implicit class ScalaRemovePermissionRequest(val request: RemovePermissionRequest) extends AnyVal {
    def asAws: aws.RemovePermissionRequest = {
      new aws.RemovePermissionRequest()
        .withFunctionName(request.functionName)
        .withStatementId(request.statementId)
    }
  }

  implicit class AwsFunctionConfiguration(val fc: aws.FunctionConfiguration) extends AnyVal {
    def asScala: FunctionConfiguration = {
      FunctionConfiguration(
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
        fc.getVpcConfig.asScala
      )
    }
  }

  implicit class ScalaFunctionConfiguration(val fc: FunctionConfiguration) extends AnyVal {
    def asAws: aws.FunctionConfiguration = {
      new aws.FunctionConfiguration()
        .withFunctionArn(fc.arn.arnString)
        .withFunctionName(fc.name)
        .withRuntime(fc.runtime.name)
        .withHandler(fc.handler)
        .withRole(fc.role)
        .withDescription(fc.description)
        .withTimeout(fc.timeout)
        .withMemorySize(fc.memory)
        .withLastModified(fc.lastModified)
        .withCodeSha256(fc.codeHash)
        .withVersion(fc.version)
        .withVpcConfig(fc.vpcConfig.asAws)
    }
  }

  implicit class AwsCodeLocation(val cl: aws.FunctionCodeLocation) extends AnyVal {
    def asScala: CodeLocation = {
      CodeLocation(
        cl.getLocation,
        cl.getRepositoryType
      )
    }
  }

  implicit class ScalaCodeLocation(val cl: CodeLocation) extends AnyVal {
    def asAws: aws.FunctionCodeLocation = {
      new aws.FunctionCodeLocation()
        .withLocation(cl.location)
        .withRepositoryType(cl.repositoryType)
    }
  }

  implicit class AwsVpcConfig(val vpcc: aws.VpcConfig) extends AnyVal {
    def asScala: VpcConfig = {
      VpcConfig(
        Option(vpcc.getSecurityGroupIds.asScala.toList),
        Option(vpcc.getSubnetIds.asScala.toList)
      )
    }
  }

  implicit class ScalaVpcConfig(val vpcc: VpcConfig) extends AnyVal {
    def asAws: aws.VpcConfig = {
      val awsVpc = new aws.VpcConfig()
      vpcc.securityGroupIds.foreach(awsVpc.withSecurityGroupIds(_: _*))
      vpcc.subnetIds.foreach(awsVpc.withSubnetIds(_: _*))
      awsVpc
    }
  }

  implicit class AwsVpcConfigResponse(val vpcc: aws.VpcConfigResponse) extends AnyVal {
    def asScala: VpcConfigResponse = {
      VpcConfigResponse(
        Option(vpcc.getSecurityGroupIds.asScala.toList).filter(_.nonEmpty),
        Option(vpcc.getSubnetIds.asScala.toList).filter(_.nonEmpty),
        Option(vpcc.getVpcId)
      )
    }
  }

  implicit class ScalaVpcConfigResponse(val vpcc: VpcConfigResponse) extends AnyVal {
    def asAws: aws.VpcConfigResponse = {
      val awsVpc = new aws.VpcConfigResponse()
      vpcc.securityGroupIds.foreach(awsVpc.withSecurityGroupIds(_: _*))
      vpcc.subnetIds.foreach(awsVpc.withSubnetIds(_: _*))
      vpcc.vpcId.foreach(awsVpc.withVpcId)
      awsVpc
    }
  }
}
