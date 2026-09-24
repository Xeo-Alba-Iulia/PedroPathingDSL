package com.pedropathing.paths

import com.pedropathing.paths.curves.Curve

@PathMarker
class CallbackBuilderScope @PublishedApi internal constructor(val curve: Curve) {
    fun build(): List<Callback> = TODO()
}