package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class MessageTypeSpec extends FreeSpec {
  val mTypes = Table("Message Types", MessageType.values: _*)

  "a MessageType" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(mTypes) { mType ⇒
          val mt = aws.MessageType.fromValue(mType.name)

          mt.asScala.asAws shouldBe mt
        }
      }

      "via its AWS equivalent" in {
        forAll(mTypes) { mt ⇒
          mt.asAws.asScala shouldBe mt
        }
      }
    }

    "has a toString that matches the ID" in {
      forAll(mTypes) { mt ⇒
        mt.toString shouldBe mt.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(mTypes) { mt ⇒
        MessageType.unapply(mt.name) shouldBe Some(mt)
      }
    }

    "can be arbitrarily generated" in {
      forAll(mTypes) { mt ⇒
        MessageType.values.contains(mt)
      }
    }
  }
}
