package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model => aws}
import com.monsanto.arch.awsutil.Arn.Namespace
import com.monsanto.arch.awsutil.Arn.Namespace.AmazonSQS
import com.monsanto.arch.awsutil.auth.policy.action.LambdaAction
import com.monsanto.arch.awsutil.{Account, Arn}
import com.monsanto.arch.awsutil.auth.policy.{Action, Principal, Resource}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import com.monsanto.arch.awsutil.testkit.{CoreGen, LambdaGen, UtilGen}
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import com.monsanto.arch.awsutil.test_support.Samplers._
import com.monsanto.arch.awsutil.testkit.CoreScalaCheckImplicits._
import com.monsanto.arch.awsutil.partitions.Partition
import com.monsanto.arch.awsutil.regions.Region
import com.monsanto.arch.awsutil.regions.Region.AP_NORTHEAST_2

class AddPermissionRequestSpec extends FreeSpec {

  case class TestArn(testPartition: Partition,
                     testNamespace: Arn.Namespace,
                     testRegion: Option[Region],
                     testAccount: Option[Account],
                     testResource: String) extends Arn(testPartition, testNamespace, testRegion, testAccount) {
    override def resource = testResource
  }
  implicit val arbTestArn: Arbitrary[TestArn] =
    Arbitrary {
      for {
        partition ← arbitrary[Partition]
        namespace ← arbitrary[Arn.Namespace]
        region ← arbitrary[Option[Region]]
        account ← Gen.option(CoreGen.accountId).map(_.map(id ⇒ Account(id, partition)))
        resource ← UtilGen.stringOf(UtilGen.asciiChar, 1, 1024).suchThat(_.nonEmpty)
      } yield TestArn(partition, namespace, region, account, resource)
    }

  implicit val shrinkTestArn: Shrink[TestArn] =
    Shrink { arn ⇒
      Shrink.shrink(arn.testResource).filter(_.nonEmpty).map(x ⇒ arn.copy(testResource = x))
    }

  "can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { request: AddPermissionRequest ⇒
        val awsRequest =
          new aws.AddPermissionRequest()
            .withAction(request.action.name)
            .withFunctionName(request.functionName)
            .withPrincipal(request.principal.id)
            .withSourceAccount(request.sourceAccount.orNull)
            .withSourceArn(request.sourceArn.map(_.arnString).orNull)
            .withStatementId(request.statementId)

        awsRequest.asScala.asAws shouldBe awsRequest
      }
    }

    "via its AWS equivalent" in {
      forAll { request: AddPermissionRequest  ⇒
        request.asAws.asScala shouldBe request
      }
    }
  }

}
