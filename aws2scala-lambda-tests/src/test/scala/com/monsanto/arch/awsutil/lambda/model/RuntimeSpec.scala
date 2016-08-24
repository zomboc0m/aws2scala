package com.monsanto.arch.awsutil.lambda.model

import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class RuntimeSpec extends FreeSpec {
  val runtimes = Table("runtimes", Runtime.values: _*)

  "a Runtime" - {
    "has a toString that matches the ID" in {
      forAll(runtimes) { r ⇒
        r.toString shouldBe r.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(runtimes) { r ⇒
        Runtime.unapply(r.name) shouldBe Some(r)
      }
    }
  }
}
