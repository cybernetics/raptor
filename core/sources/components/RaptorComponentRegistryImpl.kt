package io.fluidsonic.raptor

import kotlin.reflect.*


internal class RaptorComponentRegistryImpl(
	override val parent: RaptorComponentRegistry.Mutable? = null
) : RaptorComponentRegistry.Mutable {

	private val registrationsByClass: MutableMap<KClass<out RaptorComponent>, RaptorComponentRegistrationImpl.Collection<*>> = hashMapOf()


	override fun <Component : RaptorComponent> configureAll(clazz: KClass<Component>): RaptorComponentScope.Collection<Component> =
		getOrCreateCollection(clazz)


	override fun <Component : RaptorComponent> configureSingle(clazz: KClass<Component>): RaptorComponentScope<Component> =
		getOrCreateCollection(clazz)
			.apply {
				if (size > 1)
					error(
						"Cannot configure a single component of $clazz as $size have been registered.\n" +
							"New:$this\n" +
							"Existing: $this"
					)
			}


	override fun <Component : RaptorComponent> configureSingle(component: Component, clazz: KClass<Component>): RaptorComponentScope<Component> =
		getCollection(clazz)?.firstOrNull { it.component === component }
			?: error("Cannot get registration for component instance of ${component::class} that is not registered: $component")


	override fun <Component : RaptorComponent> getAll(clazz: KClass<Component>): List<RaptorComponentRegistration.Mutable<Component>> =
		getCollection(clazz)?.toList().orEmpty()


	@Suppress("UNCHECKED_CAST")
	private fun <Component : RaptorComponent> getCollection(clazz: KClass<Component>) =
		registrationsByClass[clazz] as RaptorComponentRegistrationImpl.Collection<Component>?


	@Suppress("UNCHECKED_CAST")
	private fun <Component : RaptorComponent> getOrCreateCollection(clazz: KClass<Component>) =
		registrationsByClass.getOrPut(clazz) { RaptorComponentRegistrationImpl.Collection<Component>() }
			as RaptorComponentRegistrationImpl.Collection<Component>


	override fun <Component : RaptorComponent> getSingle(clazz: KClass<Component>): RaptorComponentRegistration.Mutable<Component>? =
		getCollection(clazz)
			?.apply {
				if (size > 1)
					error(
						"Cannot get single component of $clazz as $size have been registered.\n" +
							"New:$this\n" +
							"Existing: $this"
					)
			}
			?.firstOrNull()


	override fun <Component : RaptorComponent> getSingle(component: Component, clazz: KClass<Component>): RaptorComponentRegistration.Mutable<Component> =
		getCollection(clazz)?.firstOrNull { it.component === component }
			?: error("Cannot get registration for component instance that is not registered: $component")


	override fun <Component : RaptorComponent> register(
		component: Component,
		clazz: KClass<Component>,
		definesScope: Boolean
	): RaptorComponentScope<Component> =
		getOrCreateCollection(clazz).addComponent(
			component = component,
			containingRegistry = this,
			registry = if (definesScope) RaptorComponentRegistryImpl(parent = this) else this
		)
}
