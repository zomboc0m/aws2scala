package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._

class OwnerSpec extends FreeSpec {
  val owners = Table("Owners", Owner.values: _*)

  "an Owner" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(owners) { owner ⇒
          val o = aws.Owner.fromValue(owner.name)

          o.asScala.asAws shouldBe o
        }
      }

      "via its AWS equivalent" in {
        forAll(owners) { o ⇒
          o.asAws.asScala shouldBe o
        }
      }
    }
  }

  "has a toString that matches the ID" in {
    forAll(owners) { o ⇒
      o.toString shouldBe o.name
    }
  }

  "can be round-tripped via its string representation" in {
    forAll(owners) { o ⇒
      Owner.unapply(o.name) shouldBe Some(o)
    }
  }

  "can be arbitrarily generated" in {
    forAll(owners) { o ⇒
      Owner.values.contains(o)
    }
  }
}
