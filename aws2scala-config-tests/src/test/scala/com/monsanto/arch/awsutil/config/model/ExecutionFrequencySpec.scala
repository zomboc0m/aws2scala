package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._

class ExecutionFrequencySpec extends FreeSpec {
  val frequencies = Table("Execution Frequencies", ExecutionFrequency.values: _*)

  "an ExecutionFrequency" - {
    "can be round-tripped" - {
      "from its AWS equivalent" in {
        forAll(frequencies) { ef ⇒
          val e = aws.MaximumExecutionFrequency.fromValue(ef.name)

          e.asScala.asAws shouldBe e
        }
      }

      "via its AWS equivalent" in {
        forAll(frequencies) { ef ⇒
          ef.asAws.asScala shouldBe ef
        }
      }
    }

    "has a toString that matches the ID" in {
      forAll(frequencies) { ef ⇒
        ef.toString shouldBe ef.name
      }
    }

    "can be round-tripped via its string representation" in {
      forAll(frequencies) { ef ⇒
        ExecutionFrequency.unapply(ef.name) shouldBe Some(ef)
      }
    }

    "can be arbitrarily generated" in {
      forAll(frequencies) { ef ⇒
        ExecutionFrequency.values.contains(ef)
      }
    }
  }
}
