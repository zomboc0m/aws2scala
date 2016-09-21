package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class ExecutionFrequencySpec extends FreeSpec {
  "an ExecutionFrequency can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(ExecutionFrequency.values.map(_.name))) { name ⇒
        val e = aws.MaximumExecutionFrequency.fromValue(name)

        e.asScala.asAws shouldBe e
      }
    }

    "via its AWS equivalent" in {
      forAll { e: ExecutionFrequency ⇒
        e.asAws.asScala shouldBe e
      }
    }
  }
}
