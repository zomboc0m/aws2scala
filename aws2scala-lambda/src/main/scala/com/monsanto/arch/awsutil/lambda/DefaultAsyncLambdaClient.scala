package com.monsanto.arch.awsutil.lambda

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.monsanto.arch.awsutil.lambda.model.{FunctionArn, GetFunctionRequest}

private[awsutil] class DefaultAsyncLambdaClient(streaming: StreamingLambdaClient) extends AsyncLambdaClient {
  override def getFunction(functionName: String)(implicit m: Materializer) =
    Source.single(GetFunctionRequest(functionName))
      .via(streaming.functionGetter)
      .runWith(Sink.head)

  override def getFunction(functionArn: FunctionArn)(implicit m: Materializer) =
    getFunction(functionArn.arnString)(m)
}
