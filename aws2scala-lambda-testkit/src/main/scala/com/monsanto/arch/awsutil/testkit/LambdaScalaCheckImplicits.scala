package com.monsanto.arch.awsutil.testkit

import java.nio.ByteBuffer

import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
import com.monsanto.arch.awsutil.lambda.Lambda
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.partitions.Partition
import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalacheck.Arbitrary.arbitrary

object LambdaScalaCheckImplicits {
  Lambda.init()

  implicit lazy val arbGetFunctionRequest: Arbitrary[GetFunctionRequest] = {
    Arbitrary {
      for {
        name ← LambdaGen.functionName
      } yield GetFunctionRequest(name)
    }
  }

  implicit lazy val arbCreateFunctionRequest: Arbitrary[CreateFunctionRequest] = {
    Arbitrary {
      for {
        function <- arbitrary[LambdaFunction]
        publish <- arbitrary[Boolean]
        code <- arbitrary[FunctionCode]
      } yield {
        CreateFunctionRequest(
          code,
          function.name,
          function.handler,
          function.runtime,
          function.role,
          function.description,
          function.memory,
          publish,
          function.timeout,
          function.vpcConfig)
      }
    }
  }

  implicit val noShrink: Shrink[CreateFunctionRequest] = Shrink.shrinkAny
  implicit val noShrinkLF: Shrink[LambdaFunction] = Shrink.shrinkAny

  implicit lazy val arbLambdaFunction: Arbitrary[LambdaFunction] =
    Arbitrary {
      for {
        rt <- arbitrary[Runtime]
        name <- LambdaGen.functionName
        handler <- UtilGen.stringOf(UtilGen.wordChar, 1, 128)
        roleName <- CoreGen.iamName
        account <- arbitrary[Account]
        region <- CoreGen.regionFor(account)
        description <- UtilGen.stringOf(UtilGen.wordChar, 1, 128)
        memory <- Gen.choose(128, 1536)
        time <- Gen.choose(1, 10)
        date <- LambdaGen.awsTimestamp
        version <- LambdaGen.versionNum
        hash <- UtilGen.stringOf(UtilGen.extendedWordChar, 1, 30)
        vpc <- arbitrary[VpcConfig]
        cl <- arbitrary[CodeLocation]
      } yield {
        val roleArn = RoleArn(account, roleName)
        val functionArn = FunctionArn(region, account, name)
        LambdaFunction(
          functionArn,
          name,
          rt,
          handler,
          roleArn.arnString,
          description,
          time,
          date,
          memory,
          version,
          hash,
          Some(vpc),
          Some(cl)
        )
      }
    }

  implicit val arbRuntime: Arbitrary[Runtime] =
    Arbitrary {
      Gen.oneOf(Runtime.values)
    }

  implicit lazy val arbCodeLocation: Arbitrary[CodeLocation] =
    Arbitrary {
      for {
        location <- UtilGen.stringOf(UtilGen.extendedWordChar, 1, 256)
        repo <- UtilGen.stringOf(UtilGen.extendedWordChar, 1, 256)
      } yield CodeLocation(location, repo)
    }

  implicit lazy val arbVpcConfig: Arbitrary[VpcConfig] =
    Arbitrary {
      for {
        size <- Gen.choose(1, 10)
        sgid <- Gen.listOfN(size, CoreGen.iamName)
        snid <- Gen.listOfN(size, CoreGen.iamName)
      } yield VpcConfig(sgid, snid)
    }


  implicit lazy val arbFunctionArn: Arbitrary[FunctionArn] =
    Arbitrary {
      for {
        functionName <- CoreGen.iamName
        account <- arbitrary[Account]
        region <- CoreGen.regionFor(account)
      } yield FunctionArn(region, account, functionName)
    }

  implicit lazy val arbFunctionCode: Arbitrary[FunctionCode] =
    Arbitrary {
      for {
        zip <- arbitrary[Boolean]
        source <- if (zip) arbZipFunctionCode else arbS3FunctionCode
      } yield source
    }

  def arbZipFunctionCode: Gen[FunctionCode] =
    for {
      bytes <- arbitrary[Array[Byte]]
    } yield FunctionCode(ZipFile = Some(ByteBuffer.wrap(bytes)))


  def arbS3FunctionCode: Gen[FunctionCode] =
    for {
      bucket <- arbitrary[String] suchThat (!_.isEmpty)
      key <- arbitrary[String] suchThat (!_.isEmpty)
      version <- arbitrary[String]
    } yield FunctionCode.fromS3Bucket(bucket, key, version)

  implicit lazy val arbArn: Arbitrary[Arn] =
    Arbitrary {
      for {
        partition ← arbitrary[Partition]
        account ← Gen.option(CoreGen.accountId).map(_.map(id ⇒ Account(id, partition)))
        region ← arbitrary[Option[Region]]
        namespace ← arbitrary[Arn.Namespace]
        resourceStr ← Gen.identifier
      } yield {
        new Arn(partition, namespace, region, account) {
          override val resource = resourceStr
        }
      }
    }

}
