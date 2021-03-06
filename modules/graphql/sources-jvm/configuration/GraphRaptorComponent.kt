package io.fluidsonic.raptor

import io.fluidsonic.raptor.graphql.internal.*


// FIXME taggable
public class GraphRaptorComponent internal constructor() : RaptorComponent.Default<GraphRaptorComponent>(), RaptorTaggableComponent {

	internal val definitions: MutableList<RaptorGraphDefinition> = mutableListOf()
	internal var includesDefaultDefinitions = false


	internal fun toGraphRoute() =
		GraphRoute(
			system = GraphSystemDefinitionBuilder.build(definitions)
				.let(GraphTypeSystemBuilder::build)
				.let(GraphSystemBuilder::build)
		)


	public companion object;


	internal object Key : RaptorComponentKey<GraphRaptorComponent> {

		override fun toString() = "graphql"
	}
}


@RaptorDsl
public fun RaptorComponentSet<GraphRaptorComponent>.definitions(vararg definitions: RaptorGraphDefinition) {
	definitions(definitions.asIterable())
}


@RaptorDsl
public fun RaptorComponentSet<GraphRaptorComponent>.definitions(definitions: Iterable<RaptorGraphDefinition>) {
	configure {
		this.definitions += definitions
	}
}


@RaptorDsl
public fun RaptorComponentSet<GraphRaptorComponent>.includeDefaultDefinitions() {
	configure {
		if (includesDefaultDefinitions)
			return@configure

		includesDefaultDefinitions = true

		definitions(RaptorGraphDefaults.definitions)
	}
}
