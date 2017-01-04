package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class RuleStateSpec extends FreeSpec {
  val ruleStates = Table("Rule States", RuleState.values: _*)

  "a RuleState" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(ruleStates) { scalaRS ⇒
          val rs = aws.ConfigRuleState.fromValue(scalaRS.name)

          rs.asScala.asAws shouldBe rs
        }
      }

      "via its AWS equivalent" in {
        forAll(ruleStates) { rs ⇒
          rs.asAws.asScala shouldBe rs
        }
      }
    }
    "has a toString that matches the ID" in {
      forAll(ruleStates) { rs ⇒
        rs.toString shouldBe rs.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(ruleStates) { rs ⇒
        RuleState.unapply(rs.name) shouldBe Some(rs)
      }
    }

    "can be arbitrarily generated" in {
      forAll(ruleStates) { rs ⇒
        RuleState.values.contains(rs)
      }
    }
  }
}
