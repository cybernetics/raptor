package io.fluidsonic.raptor

import io.fluidsonic.raptor.graphql.internal.*


// FIXME customize value definitions
@RaptorDsl
public class RaptorEnumGraphDefinitionBuilder<Type : Enum<Type>> internal constructor(
	kotlinType: KotlinType,
	name: String,
	private val stackTrace: List<StackTraceElement>,
	private val values: Collection<Type>,
) :
	RaptorNamedGraphTypeDefinitionBuilder<Type>(
		kotlinType = kotlinType,
		name = name
	) {


	override fun build(description: String?) =
		EnumGraphDefinition(
			additionalDefinitions = emptyList(),
			description = description,
			isInput = true,
			isOutput = true,
			kotlinType = kotlinType,
			name = name,
			parse = { name ->
				this@RaptorEnumGraphDefinitionBuilder.values.firstOrNull { it.name == name }
					?: invalid(details = "valid values: ${this@RaptorEnumGraphDefinitionBuilder.values.sortedBy { it.name }.joinToString(separator = ", ") { it.name }}")
			}, // FIXME leak. rework
			serialize = { (it as Type).name }, // FIXME rework
			stackTrace = stackTrace,
			values = values.mapTo(hashSetOf()) { it.name }
		)
}
