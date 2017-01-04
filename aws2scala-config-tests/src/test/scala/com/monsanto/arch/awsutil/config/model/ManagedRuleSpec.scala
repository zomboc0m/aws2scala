package com.monsanto.arch.awsutil.config.model

import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class ManagedRuleSpec extends FreeSpec {
  val managedRules = Table("Managed Rules", ManagedRule.values: _*)

  "a Runtime" - {
    "has a toString that matches the ID" in {
      forAll(managedRules) { r ⇒
        r.toString shouldBe r.identifier
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(managedRules) { r ⇒
        ManagedRule.unapply(r.identifier) shouldBe Some(r)
      }
    }

    "can be arbitrarily generated" in {
      forAll(managedRules) { r ⇒
        ManagedRule.values.contains(r)
      }
    }
  }
}
