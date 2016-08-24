package com.monsanto.arch.awsutil.lambda

import com.monsanto.arch.awsutil.test_support.AwsClientProviderBehaviours
import org.scalamock.scalatest.MockFactory
import org.scalatest.FreeSpec

class LambdaSpec extends FreeSpec with MockFactory with AwsClientProviderBehaviours {
  "the Lambda provider should" - {
    behave like anAwsClientProvider(Lambda)
  }
}
