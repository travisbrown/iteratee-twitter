package io.iteratee.twitter

import cats.MonadError
import com.twitter.util.Try
import io.catbird.util.twitterTryInstance
import io.iteratee.modules.{ EnumerateeModule, EnumeratorErrorModule, IterateeErrorModule, Module }

trait TryModule extends Module[Try]
    with EnumerateeModule[Try]
    with EnumeratorErrorModule[Try, Throwable]
    with IterateeErrorModule[Try, Throwable] {
  final type M[f[_]] = MonadError[f, Throwable]

  final protected val F: MonadError[Try, Throwable] = twitterTryInstance
}
