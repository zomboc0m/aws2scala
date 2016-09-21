package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class EventSourceSpec extends FreeSpec {
  "an EventSource can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll(Gen.oneOf(EventSource.values.map(_.name))) { name ⇒
        val es = aws.EventSource.fromValue(name)

        es.asScala.asAws shouldBe es
      }
    }

    "via its AWS equivalent" in {
      forAll { es: EventSource ⇒
        es.asAws.asScala shouldBe es
      }
    }
  }
}

