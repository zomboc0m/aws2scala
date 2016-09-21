package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class MessageTypeSpec extends FreeSpec {
  "a MessageType can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(MessageType.values.map(_.name))) { name ⇒
        val mt = aws.MessageType.fromValue(name)

        mt.asScala.asAws shouldBe mt
      }
    }

    "via its AWS equivalent" in {
      forAll { mt: MessageType ⇒
        mt.asAws.asScala shouldBe mt
      }
    }
  }
}
