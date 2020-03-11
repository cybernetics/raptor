package io.fluidsonic.raptor

import org.kodein.di.*


@Raptor.Dsl3
class RaptorFeatureSetupImpl internal constructor(
	context: RaptorComponentRegistry
) : RaptorFeatureComponent {

	private val kodeinConfigs: MutableList<Kodein.Builder.() -> Unit> = mutableListOf()
	private val startCallbacks: MutableList<suspend RaptorScope.() -> Unit> = mutableListOf()
	private val stopCallbacks: MutableList<suspend RaptorScope.() -> Unit> = mutableListOf()

	override val raptorComponentRegistry = context


	// FIXME duplicates
	override fun install(feature: RaptorFeature) {
		with(feature) {
			setup()
		}
	}


	override fun kodein(config: Kodein.Builder.() -> Unit) {
		kodeinConfigs += config
	}


	override fun onStart(callback: suspend RaptorScope.() -> Unit) {
		startCallbacks += callback
	}


	override fun onStop(callback: suspend RaptorScope.() -> Unit) {
		stopCallbacks += callback
	}
}
