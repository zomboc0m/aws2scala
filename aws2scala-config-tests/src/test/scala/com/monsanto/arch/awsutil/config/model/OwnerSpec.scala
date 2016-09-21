package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class OwnerSpec extends FreeSpec {
  "an Owner can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(Owner.values.map(_.name))) { name ⇒
        val o = aws.Owner.fromValue(name)

        o.asScala.asAws shouldBe o
      }
    }

    "via its AWS equivalent" in {
      forAll { o: Owner ⇒
        o.asAws.asScala shouldBe o
      }
    }
  }
}
