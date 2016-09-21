package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class RuleStateSpec extends FreeSpec {
  "a RuleState can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(RuleState.values.map(_.name))) { name ⇒
        val rs = aws.ConfigRuleState.fromValue(name)

        rs.asScala.asAws shouldBe rs
      }
    }

    "via its AWS equivalent" in {
      forAll { rs: RuleState ⇒
        rs.asAws.asScala shouldBe rs
      }
    }
  }
}
