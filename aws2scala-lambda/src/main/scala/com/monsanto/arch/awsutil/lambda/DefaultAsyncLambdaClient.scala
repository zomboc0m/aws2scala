package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.lambda.model.{CreateFunctionRequest, _}

private[awsutil] class DefaultAsyncLambdaClient(streaming: StreamingLambdaClient) extends AsyncLambdaClient {
  override def createFunction(pathToCode: String, name: String, handler: String, role: String, runtime: Runtime)(implicit m: Materializer) =
    createFunction(CreateFunctionRequest(FunctionCode.fromZipFile(pathToCode), name, handler, runtime, role))

  override def createFunction(code: FunctionCode, name: String, handler: String, role: String, runtime: Runtime)(implicit m: Materializer) =
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
}
