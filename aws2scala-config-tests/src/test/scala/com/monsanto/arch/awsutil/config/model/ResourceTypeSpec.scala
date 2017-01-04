package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class ResourceTypeSpec extends FreeSpec {
  val resourceTypes = Table("Resource Types", ResourceType.values: _*)

  "a ResourceType" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(resourceTypes) { rt ⇒
          val awsRT = aws.ResourceType.fromValue(rt.toString)

          awsRT.asScala.asAws shouldBe awsRT
        }
      }

      "via its AWS equivalent" in {
        forAll(resourceTypes) { rt ⇒
          rt.asAws.asScala shouldBe rt
        }
      }
    }

    "has a toString that matches the ID" in {
      forAll(resourceTypes) { rt ⇒
        rt.toString shouldBe rt.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(resourceTypes) { rt ⇒
        ResourceType.unapply(rt.name) shouldBe Some(rt)
      }
    }

    "can be arbitrarily generated" in {
      forAll(resourceTypes) { rt ⇒
        ResourceType.values.contains(rt)
      }
    }
  }
}
