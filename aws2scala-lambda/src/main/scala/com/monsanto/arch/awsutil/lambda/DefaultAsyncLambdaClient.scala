package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.auth.policy.{Action, Principal}
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, _}

private[awsutil] class DefaultAsyncLambdaClient(streaming: StreamingLambdaClient) extends AsyncLambdaClient {
  override def createFunction(pathToCode: String, name: String, handler: String, role: RoleArn, runtime: Runtime)(implicit m: Materializer) =
    createFunction(CreateFunctionRequest(FunctionCode.fromZipFile(pathToCode), name, handler, runtime, role))

  override def createFunction(code: FunctionCode, name: String, handler: String, role: RoleArn, runtime: Runtime)(implicit m: Materializer) =
    createFunction(CreateFunctionRequest(code, name, handler, runtime, role))

  override def createFunction(request: CreateFunctionRequest)(implicit m: Materializer) =
    Source.single(request)
      .via(streaming.functionCreator)
      .runWith(Sink.head)

  override def deleteFunction(name: String)(implicit m: Materializer) =
    Source.single(name)
      .via(streaming.functionDeleter)
      .runWith(Sink.ignore)

  override def getFunction(functionName: String)(implicit m: Materializer) =
    Source.single(GetFunctionRequest(functionName))
      .via(streaming.functionGetter)
      .runWith(Sink.head)

  override def getFunction(functionArn: FunctionArn)(implicit m: Materializer) =
    getFunction(functionArn.arnString)(m)

  override def addPermission(statementId: String, functionName: String, principal: Principal, action: Action)(implicit m: Materializer) =
    Source.single(AddPermissionRequest(statementId, functionName, principal, action))
      .via(streaming.permissionAdder)
      .runWith(Sink.head)

  override def addPermission(statementId: String, functionName: String, principal: Principal, action: Action, sourceArn: Arn, sourceAccount: Account)(implicit m: Materializer) =
    Source.single(AddPermissionRequest(statementId, functionName, principal, action, Some(sourceArn.arnString), Some(sourceAccount.id)))
      .via(streaming.permissionAdder)
      .runWith(Sink.head)

  override def removePermission(statementId: String, functionName: String)(implicit m: Materializer) =
    Source.single(RemovePermissionRequest(statementId, functionName))
      .via(streaming.permissionRemover)
      .runWith(Sink.ignore)

  override def getPolicy(functionName: String)(implicit m: Materializer) =
    Source.single(functionName)
      .via(streaming.policyGetter)
      .runWith(Sink.head)
}
