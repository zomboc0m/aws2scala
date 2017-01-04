package com.monsanto.arch.awsutil.lambda.model

import com.amazonaws.services.lambda.{model ⇒ aws}
import com.monsanto.arch.awsutil.converters.LambdaConverters._
import com.monsanto.arch.awsutil.testkit.LambdaScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import scala.collection.JavaConversions._

class VpcConfigSpec extends FreeSpec {
  "a vpc config" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll { scalaVPCC: VpcConfig ⇒
          val awsVPCC =
            new aws.VpcConfig()
          scalaVPCC.securityGroupIds.foreach(awsVPCC.withSecurityGroupIds(_))
          scalaVPCC.subnetIds.foreach(awsVPCC.withSubnetIds(_))

          awsVPCC.asScala.asAws shouldBe awsVPCC
        }
      }

      "via its AWS equivalent" in {
        forAll { scalaVPCC: VpcConfig  ⇒
          scalaVPCC.asAws.asScala shouldBe scalaVPCC
        }
      }
    }
  }
  "a vpc config response" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll { scalaVPCCR: VpcConfigResponse ⇒
          val awsVPCCR =
            new aws.VpcConfigResponse()
          scalaVPCCR.securityGroupIds.foreach(awsVPCCR.withSecurityGroupIds(_))
          scalaVPCCR.subnetIds.foreach(awsVPCCR.withSubnetIds(_))
          scalaVPCCR.vpcId.foreach(awsVPCCR.withVpcId)

          awsVPCCR.asScala.asAws shouldBe awsVPCCR
        }
      }

      "via its AWS equivalent" in {
        forAll { scalaVPCCR: VpcConfigResponse  ⇒
          scalaVPCCR.asAws.asScala shouldBe scalaVPCCR
        }
      }
    }
  }
}
