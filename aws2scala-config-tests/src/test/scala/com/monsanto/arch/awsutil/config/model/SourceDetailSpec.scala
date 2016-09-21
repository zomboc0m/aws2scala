package com.monsanto.arch.awsutil.config.model

import com.amazonaws.services.config.{model => aws}
import com.monsanto.arch.awsutil.converters.ConfigConverters._
import com.monsanto.arch.awsutil.testkit.ConfigScalaCheckImplicits._
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

class SourceDetailSpec extends FreeSpec {
  "a SourceDetail can be round-tripped" - {
    "from its AWS equivalent" in {
      forAll { (es: EventSource, ef: ExecutionFrequency, mt: MessageType) ⇒
        val sd = new aws.SourceDetail().
          withEventSource(es.asAws).
          withMaximumExecutionFrequency(ef.asAws).
          withMessageType(mt.asAws)

        sd.asScala.asAws shouldBe sd
      }
    }

    "via its AWS equivalent" in {
      forAll { sd: SourceDetail ⇒
        sd.asAws.asScala shouldBe sd
      }
    }
  }
}
