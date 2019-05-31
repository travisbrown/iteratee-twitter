package io.iteratee.twitter

import cats.MonadError
import com.twitter.util.Future
import io.catbird.util.twitterFutureInstance
import io.iteratee.modules.{ EnumerateeModule, EnumeratorErrorModule, IterateeErrorModule, Module }

trait FutureModule
    extends Module[Future]
    with EnumerateeModule[Future]
    with EnumeratorErrorModule[Future, Throwable]
    with IterateeErrorModule[Future, Throwable] {
  final type M[f[_]] = MonadError[f, Throwable]

  final protected val F: MonadError[Future, Throwable] = twitterFutureInstance
}
