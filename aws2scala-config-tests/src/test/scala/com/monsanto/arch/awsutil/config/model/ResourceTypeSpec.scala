package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class ResourceTypeSpec extends FreeSpec {
  "a ResourceType can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(ResourceType.values.map(_.toString))) { name ⇒
        val rt = aws.ResourceType.fromValue(name)

        rt.asScala.asAws shouldBe rt
      }
    }

    "via its AWS equivalent" in {
      forAll { rt: ResourceType ⇒
        rt.asAws.asScala shouldBe rt
      }
    }
  }
}
