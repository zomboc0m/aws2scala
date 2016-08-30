package com.monsanto.arch.awsutil.testkit

import com.monsanto.arch.awsutil.Account
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
import com.monsanto.arch.awsutil.lambda.Lambda
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object LambdaScalaCheckImplicits {
  Lambda.init()

  implicit lazy val arbGetFunctionRequest: Arbitrary[GetFunctionRequest] = {
    Arbitrary{
      for {
        name ← LambdaGen.functionName
      } yield GetFunctionRequest(name)
    }
  }

  implicit lazy val arbLambdaFunction: Arbitrary[LambdaFunction] =
    Arbitrary{
      for {
        rt <- arbitrary[Runtime]
        name <- LambdaGen.functionName
        handler <- UtilGen.stringOf(UtilGen.wordChar,1,128)
        roleName <- CoreGen.iamName
        account <- arbitrary[Account]
        region <- CoreGen.regionFor(account)
        description <- UtilGen.stringOf(UtilGen.wordChar,1,128)
        time <- Gen.choose(1,10)
        date <- LambdaGen.awsTimestamp
        version <- LambdaGen.versionNum
        hash <- UtilGen.stringOf(UtilGen.extendedWordChar,1,30)
        location <- UtilGen.stringOf(UtilGen.extendedWordChar,1,256)
        repo <- UtilGen.stringOf(UtilGen.extendedWordChar,1,256)
      } yield {
        val roleArn = RoleArn(account,roleName)
        val functionArn = FunctionArn(region,account,name)
        LambdaFunction(
          functionArn,
          name,
          rt,
          handler,
          roleArn.arnString,
          description,
          time,
          date,
          version,
          hash,
          CodeLocation(location,repo)
        )
      }
    }

  implicit def arbRuntime: Arbitrary[Runtime] =
    Arbitrary{
      Gen.oneOf(Runtime.values)
    }


  implicit lazy val arbFunctionArn: Arbitrary[FunctionArn] =
    Arbitrary {
      for{
        functionName <- CoreGen.iamName
        account <- arbitrary[Account]
        region <- CoreGen.regionFor(account)
      } yield FunctionArn(region, account, functionName)
    }

}
