// https://youtrack.jetbrains.com/issue/KT-12495
@file:JvmName("Currency@graph")

package io.fluidsonic.raptor

import io.fluidsonic.stdlib.*


fun Currency.Companion.graphDefinition() = graphScalarDefinition {
	conversion {
		parseString(::byCode)

		parseJson(::byCode)
		serializeJson(Currency::code)
	}
}