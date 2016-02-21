package com.monsanto.arch.awsutil

import java.util.concurrent.{Future ⇒ JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import org.scalamock.function.FunctionAdapter2
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory

trait AwsMockUtils { this: MockFactory ⇒
  implicit class EnhancedCallHandler[Request <: AmazonWebServiceRequest, Result](call: CallHandler2[Request, AsyncHandler[Request,Result], JFuture[Result]]) {
    def withAwsSuccess(result: ⇒ Result): CallHandler2[Request, AsyncHandler[Request,Result], JFuture[Result]] = {
      call.onCall { (request, handler) ⇒
        handler.onSuccess(request, result)
        mock[JFuture[Result]]
      }
    }

    def withAwsSuccess(f: Request ⇒ Result): CallHandler2[Request, AsyncHandler[Request,Result], JFuture[Result]] = {
      call.onCall { (request, handler) ⇒
        handler.onSuccess(request, f(request))
        mock[JFuture[Result]]
      }
    }

    def withVoidAwsSuccess(): CallHandler2[Request, AsyncHandler[Request,Result], JFuture[Result]] = {
      call.onCall { (request, handler) ⇒
        handler.onSuccess(request, null.asInstanceOf[Result])
        mock[JFuture[Result]]
      }
    }

    def withAwsError(error: Exception): CallHandler2[Request, AsyncHandler[Request,Result], JFuture[Result]] = {
      call.onCall { (request, handler) ⇒
        handler.onError(error)
        mock[JFuture[Result]]
      }
    }
  }

  def whereRequest[Request <: AmazonWebServiceRequest,Result](test: Request ⇒ Boolean): FunctionAdapter2[Request,AsyncHandler[Request,Result],Boolean] = {
    where((r: Request, _: AsyncHandler[Request,Result]) ⇒ test(r))
  }
}
