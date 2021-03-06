package io.fluidsonic.raptor


public object KtorRaptorFeature : RaptorFeature {

	override val id = raptorKtorFeatureId


	override fun RaptorFeatureConfigurationScope.beginConfiguration() {
		install(RaptorLifecycleFeature)
		install(RaptorTransactionFeature)

		componentRegistry.register(KtorRaptorComponent.Key, KtorRaptorComponent(globalScope = this))

		// HoconApplicationConfig(ConfigFactory.defaultApplication().resolve()!!) // FIXME

		// FIXME prevent multi-start
		lifecycle {
			onStart {
				checkNotNull(context[Ktor.PropertyKey]).start()
			}

			onStop {
				checkNotNull(context[Ktor.PropertyKey]).stop()
			}
		}
	}
}


public const val raptorKtorFeatureId: RaptorFeatureId = "raptor.ktor"


@RaptorDsl
public val RaptorTopLevelConfigurationScope.ktor: RaptorComponentSet<KtorRaptorComponent>
	get() = componentRegistry.configure(KtorRaptorComponent.Key)
