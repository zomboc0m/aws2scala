package com.monsanto.arch.awsutil.testkit

import com.monsanto.arch.awsutil.lambda.Lambda
import com.monsanto.arch.awsutil.lambda.model.GetFunctionRequest
import org.scalacheck.Arbitrary

object LambdaScalaCheckImplicits {
  Lambda.init()

  implicit lazy val arbGetFunctionRequest: Arbitrary[GetFunctionRequest] = {
    Arbitrary{
      for {
        name ← CoreGen.iamName
      } yield GetFunctionRequest(name)
    }
  }
}
