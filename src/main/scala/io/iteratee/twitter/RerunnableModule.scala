package io.iteratee.twitter

import cats.effect.Sync
import io.catbird.util.Rerunnable
import io.catbird.util.effect.rerunnableEffectInstance
import io.iteratee.modules.{ EnumerateeModule, EnumeratorErrorModule, IterateeErrorModule, Module }
import io.iteratee.files.modules.FileModule

trait RerunnableModule extends Module[Rerunnable]
    with EnumerateeModule[Rerunnable]
    with EnumeratorErrorModule[Rerunnable, Throwable]
    with IterateeErrorModule[Rerunnable, Throwable]
    with FileModule[Rerunnable] {
  final type M[f[_]] = Sync[f]

  final protected val F: Sync[Rerunnable] = rerunnableEffectInstance
}
