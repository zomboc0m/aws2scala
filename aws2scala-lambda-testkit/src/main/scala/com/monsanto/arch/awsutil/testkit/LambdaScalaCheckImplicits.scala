package com.monsanto.arch.awsutil.testkit

import java.nio.ByteBuffer

import com.monsanto.arch.awsutil.auth.policy.{Action, Principal, Resource}
import com.monsanto.arch.awsutil.auth.policy.action.LambdaAction
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.identitymanagement.model.RoleArn
import com.monsanto.arch.awsutil.lambda.Lambda
import com.monsanto.arch.awsutil.lambda.model._
import com.monsanto.arch.awsutil.partitions.Partition
import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import org.scalacheck.{Arbitrary, Gen}
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
        rt <- arbitrary[Runtime]
        name <- LambdaGen.functionName
        handler <- UtilGen.stringOf(UtilGen.wordChar, 1, 128)
        roleName <- CoreGen.iamName
        account <- arbitrary[Account]
        region <- CoreGen.regionFor(account)
        description <- Gen.option(UtilGen.stringOf(UtilGen.wordChar, 1, 128))
        memory <- Gen.option(Gen.choose(128, 1536))
        timeout <- Gen.option(Gen.choose(1, 10))
        date <- Gen.option(LambdaGen.awsTimestamp)
        version <- LambdaGen.versionNum
        hash <- UtilGen.stringOf(UtilGen.extendedWordChar, 1, 30)
        vpc <- Gen.option(arbitrary[VpcConfig])
        publish <- Gen.option(arbitrary[Boolean])
        code <- arbitrary[FunctionCode]
      } yield {
        val roleArn = RoleArn(account, roleName)
        CreateFunctionRequest(
          code,
          name,
          handler,
          rt,
          roleArn,
          description,
          memory,
          publish,
          timeout,
          vpc)
      }
    }
  }

  implicit lazy val arbGetFunctionResult: Arbitrary[GetFunctionResult] =
    Arbitrary {
      for {
        config <- arbitrary[FunctionConfiguration]
        cl <- arbitrary[CodeLocation]
      } yield GetFunctionResult(config,cl)

    }

  implicit lazy val arbLambdaFunctionConfiguration: Arbitrary[FunctionConfiguration] =
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
        vpc <- arbitrary[VpcConfigResponse]
      } yield {
        val roleArn = RoleArn(account, roleName)
        val functionArn = FunctionArn(region, account, name)
        FunctionConfiguration(
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
          vpc
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

  implicit lazy val  arbVpcConfig: Arbitrary[VpcConfig] =
    Arbitrary {
      for {
        nSecIds <- Gen.choose(1,5)
        nSubIds <- Gen.choose(1,16)
        secIds <- Gen.option(Gen.listOfN(nSecIds,UtilGen.nonEmptyString))
        subIds <- Gen.option(Gen.listOfN(nSubIds,UtilGen.nonEmptyString))
      } yield VpcConfig(secIds,subIds)
    }

  implicit lazy val  arbVpcConfigResponse: Arbitrary[VpcConfigResponse] =
    Arbitrary {
      for {
        config <- arbitrary[VpcConfig]
        vpcId <- Gen.option(UtilGen.nonEmptyString)
      } yield VpcConfigResponse(config.securityGroupIds,config.subnetIds,vpcId)
    }

  implicit lazy val arbAddPermissionRequest: Arbitrary[AddPermissionRequest] =
    Arbitrary {
      for {
        sId <- UtilGen.stringOf(UtilGen.wordChar,1,100)
        name <- LambdaGen.functionName
        principal <- Gen.oneOf(arbitrary[Principal.ServicePrincipal],arbitrary[Principal.AccountPrincipal])
        action <- Gen.oneOf(LambdaAction.values)
        r <- arbitrary[Resource]
        sourceArn = Option(Arn.fromArnString(r.id))
        sourceAccount <- Gen.option(CoreGen.accountId)
      } yield AddPermissionRequest(sId,name,principal,action,sourceArn,sourceAccount)
    }

  implicit lazy val arbCreateFunctionResult: Arbitrary[CreateFunctionResult] =
    Arbitrary {
      for {
        arn <- arbitrary[FunctionArn]
        name <- LambdaGen.functionName
        runtime <- arbitrary[Runtime]
        handler <- arbitrary[String]
        role <- arbitrary[String]
        description <- arbitrary[String]
        timeout <- arbitrary[Int]
        lastModified <- arbitrary[String]
        memory <- arbitrary[Int]
        version <- arbitrary[String]
        codeHash <- arbitrary[String]
        vpcConfig <- arbitrary[VpcConfigResponse]
      } yield CreateFunctionResult(arn, name, runtime, handler, role, description, timeout, lastModified, memory, version, codeHash, vpcConfig)
    }

  implicit lazy val arbRemovePermissionRequest: Arbitrary[RemovePermissionRequest] =
    Arbitrary {
      for {
        sId <- UtilGen.stringOf(UtilGen.wordChar,1,100)
        name <- LambdaGen.functionName
      } yield RemovePermissionRequest(sId,name)
    }
}
