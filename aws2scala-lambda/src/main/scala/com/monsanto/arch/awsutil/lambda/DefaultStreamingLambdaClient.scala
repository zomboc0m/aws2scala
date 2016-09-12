package com.monsanto.arch.awsutil.lambda

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.amazonaws.services.lambda.{AWSLambdaAsync, model => aws}
import com.fasterxml.jackson.core.JsonFactory
import com.monsanto.arch.awsutil.auth.policy.{Statement, PolicyJsonSupport}
import com.monsanto.arch.awsutil.lambda.model.{AddPermissionRequest, CreateFunctionRequest, GetFunctionRequest}
import com.monsanto.arch.awsutil.{AWSFlow, AWSFlowAdapter}
import com.monsanto.arch.awsutil.converters.LambdaConverters._

class DefaultStreamingLambdaClient(lambda: AWSLambdaAsync) extends StreamingLambdaClient {
  override val functionCreator =
    Flow[CreateFunctionRequest]
      .map(_.asAws)
      .via[aws.CreateFunctionResult, NotUsed](AWSFlow.simple(lambda.createFunctionAsync))
      .map(_.asScala)
      .named("Lambda.functionCreator")

  override val functionDeleter =
    Flow[String]
      .map(n ⇒ new aws.DeleteFunctionRequest().withFunctionName(n))
      .via(AWSFlow.simple(AWSFlowAdapter.returnInput[aws.DeleteFunctionRequest, aws.DeleteFunctionResult](lambda.deleteFunctionAsync)))
      .map(_.getFunctionName)
      .named("Lambda.functionDeleter")

  override val functionGetter =
    Flow[GetFunctionRequest]
      .map(_.asAws)
      .via[aws.GetFunctionResult, NotUsed](AWSFlow.simple(lambda.getFunctionAsync))
      .map(_.asScala)
      .named("Lambda.functionGetter")

  override val permissionAdder =
    Flow[AddPermissionRequest]
      .map(_.asAws)
      .via[aws.AddPermissionResult, NotUsed](AWSFlow.simple(lambda.addPermissionAsync))
      .map(r => extractStatementContents(r.getStatement))

  //the format of the string returned by the AddPermissionResult object is different from
  // the strings our parser expects, so we need to fix that before parsing
  def extractStatementContents(s: String) = {
    val statementContentPattern = "\\{\"Statement\":\\s*(.*)\\}".r
    val output = s match {
      case statementContentPattern(stmt) => stmt
      case _ => s
    }
    jsonToStatement(output)
  }

  def jsonToStatement(s: String): Statement = {
    val jsonFactory = new JsonFactory
    val parser = jsonFactory.createParser(s)
    PolicyJsonSupport.jsonToStatement(parser)
  }
}

