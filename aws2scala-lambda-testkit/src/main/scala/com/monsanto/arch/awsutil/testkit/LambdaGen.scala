package com.monsanto.arch.awsutil.testkit

import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.Account
import com.monsanto.arch.awsutil.auth.policy.Policy
import com.monsanto.arch.awsutil.auth.policy.PolicyDSL._
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.converters.LambdaConverters.ScalaVpcConfigResponse
import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.testkit.UtilGen._
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits.arbVpcConfigResponse
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

object LambdaGen {
  val functionName = stringOf(wordChar, 1, 140)

  def nLengthNumString(n: Int) = Gen.listOfN(n, Gen.oneOf('0' to '9')).map(_.mkString)

  val versionChar: Gen[Char] = Gen.oneOf(('0' to '9') :+ '.')

  val versionNum: Gen[String] = stringOf(versionChar, 1, 1024).suchThat(_.head != '.')

  //Example timestamp 2016-07-28T14:20:24.314+0000
  //regex "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+0000$"
  val awsTimestamp: Gen[String] =
    for {
      year <- nLengthNumString(4)
      month <- nLengthNumString(2)
      day <- nLengthNumString(2)
      hour <- nLengthNumString(2)
      minute <- nLengthNumString(2)
      second <- nLengthNumString(2)
      fracsec <- nLengthNumString(3)
    } yield {
      s"""$year-$month-${day}T$hour:$minute:$second.$fracsec+0000"""
    }

  def createFunctionResultFor(function: CreateFunctionResult): aws.CreateFunctionResult = {
    new aws.CreateFunctionResult()
      .withCodeSha256(function.codeHash)
      .withDescription(function.description)
      .withFunctionArn(function.arn.arnString)
      .withFunctionName(function.name)
      .withHandler(function.handler)
      .withLastModified(function.lastModified)
      .withMemorySize(function.memory)
      .withRole(function.role)
      .withRuntime(function.runtime.toString)
      .withTimeout(function.timeout)
      .withVersion(function.version)
      .withVpcConfig(function.vpcConfig.asAws)
  }

  def createdFunction(request: CreateFunctionRequest): Gen[CreateFunctionResult] = {
    for {
      region <- arbitrary[Region]
      account <- arbitrary[Account]
      date <- awsTimestamp
      version <- versionNum
      hash <- UtilGen.stringOf(UtilGen.extendedWordChar, 1, 30)
      vpc <- arbitrary[VpcConfigResponse]
    } yield {
      val functionArn = FunctionArn(region, account, request.name)
      CreateFunctionResult(
        functionArn,
        request.name,
        request.runtime,
        request.handler,
        request.role.arnString,
        request.description.getOrElse(""),
        request.timeout.getOrElse(3),
        date,
        request.memory.getOrElse(128),
        version,
        hash,
        vpc)
    }
  }

  def policyFor(request: AddPermissionRequest): Policy = {
    policy(
      id(request.statementId),
      statements(
        allow(
          principals(request.principal),
          actions(request.action)
        )
      )
    )
  }

}
