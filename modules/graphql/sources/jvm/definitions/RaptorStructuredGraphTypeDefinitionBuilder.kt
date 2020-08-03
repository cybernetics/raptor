package io.fluidsonic.raptor

import io.fluidsonic.raptor.graphql.internal.*
import io.fluidsonic.stdlib.*
import kotlin.reflect.*


public abstract class RaptorStructuredGraphTypeDefinitionBuilder<Value : Any> internal constructor(
	kotlinType: KotlinType,
	name: String,
) : RaptorNamedGraphTypeDefinitionBuilder<Value>(
	kotlinType = kotlinType,
	name = name
) {

	protected val nestedDefinitions: MutableList<RaptorNamedGraphTypeDefinitionBuilder<*>> = mutableListOf()


	internal abstract fun build(description: String?, additionalDefinitions: Collection<RaptorGraphDefinition>): RaptorGraphDefinition


	final override fun build(description: String?): RaptorGraphDefinition =
		build(
			description = description,
			additionalDefinitions = nestedDefinitions.map { it.build() }
		)


	@RaptorDsl
	public fun nested(configure: NestedBuilder.() -> Unit) {
		NestedBuilder().apply(configure)
	}


	@RaptorDsl
	public inner class NestedBuilder internal constructor() {

		// FIXME use global names (graphEnumDefinition) & scope or else users may accidentally use the wrong one!
		@OptIn(ExperimentalStdlibApi::class)
		@RaptorDsl
		public inline fun <reified Type : Enum<Type>> enumDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			@BuilderInference noinline configure: RaptorEnumGraphDefinitionBuilder<Type>.() -> Unit = {},
		) {
			enumDefinition(
				name = name,
				type = typeOf<Type>(),
				values = enumValues<Type>().toList(),
				configure = configure
			)
		}


		@RaptorDsl
		public fun <Type : Enum<Type>> enumDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			type: KType,
			values: List<Type>, // FIXME validate
			configure: RaptorEnumGraphDefinitionBuilder<Type>.() -> Unit = {},
		) {
			nestedDefinitions += RaptorEnumGraphDefinitionBuilder(
				kotlinType = KotlinType.of(type, requireSpecialization = true, allowMaybe = false, allowNull = false),
				name = RaptorGraphDefinition.resolveName(
					name,
					defaultNamePrefix = this@RaptorStructuredGraphTypeDefinitionBuilder.name,
					type = type
				),
				stackTrace = stackTrace(skipCount = 1),
				values = values
			)
				.apply(configure)
		}


		@OptIn(ExperimentalStdlibApi::class)
		@RaptorDsl
		public inline fun <reified Type : Any> inputObjectDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			@BuilderInference noinline configure: RaptorInputObjectGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			inputObjectDefinition(
				name = name,
				type = typeOf<Type>(),
				configure = configure
			)
		}


		@RaptorDsl
		public fun <Type : Any> inputObjectDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			type: KType,
			configure: RaptorInputObjectGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			nestedDefinitions += RaptorInputObjectGraphDefinitionBuilder<Type>(
				kotlinType = KotlinType.of(type, requireSpecialization = false, allowMaybe = false, allowNull = false),
				name = RaptorGraphDefinition.resolveName(
					name,
					defaultNamePrefix = this@RaptorStructuredGraphTypeDefinitionBuilder.name,
					type = type
				),
				stackTrace = stackTrace(skipCount = 1)
			)
				.apply(configure)
		}


		@OptIn(ExperimentalStdlibApi::class)
		@RaptorDsl
		public inline fun <reified Type : Any> interfaceDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			@BuilderInference noinline configure: RaptorInterfaceGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			interfaceDefinition(
				name = name,
				type = typeOf<Type>(),
				configure = configure
			)
		}


		@RaptorDsl
		public fun <Type : Any> interfaceDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			type: KType,
			configure: RaptorInterfaceGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			// FIXME building lazily removes definition-site from the stack trace for errors thrown by the builder
			nestedDefinitions += RaptorInterfaceGraphDefinitionBuilder<Type>(
				kotlinType = KotlinType.of(type, requireSpecialization = false, allowMaybe = false, allowNull = false),
				name = RaptorGraphDefinition.resolveName(
					name,
					defaultNamePrefix = this@RaptorStructuredGraphTypeDefinitionBuilder.name,
					type = type
				),
				stackTrace = stackTrace(skipCount = 1)
			)
				.apply(configure)
		}


		@OptIn(ExperimentalStdlibApi::class)
		@RaptorDsl
		public inline fun <reified Type : Any> objectDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			@BuilderInference noinline configure: RaptorObjectGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			objectDefinition(
				name = name,
				type = typeOf<Type>(),
				configure = configure
			)
		}


		@RaptorDsl
		public fun <Type : Any> objectDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			type: KType,
			configure: RaptorObjectGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			nestedDefinitions += RaptorObjectGraphDefinitionBuilder<Type>(
				kotlinType = KotlinType.of(type, requireSpecialization = false, allowMaybe = false, allowNull = false),
				name = RaptorGraphDefinition.resolveName(
					name,
					defaultNamePrefix = this@RaptorStructuredGraphTypeDefinitionBuilder.name,
					type = type
				),
				stackTrace = stackTrace(skipCount = 1)
			)
				.apply(configure)
		}


		@OptIn(ExperimentalStdlibApi::class)
		@RaptorDsl
		public inline fun <reified Type : Any> scalarDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			@BuilderInference noinline configure: RaptorScalarGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			scalarDefinition(
				name = name,
				type = typeOf<Type>(),
				configure = configure
			)
		}


		@RaptorDsl
		public fun <Type : Any> scalarDefinition(
			name: String = RaptorGraphDefinition.defaultName,
			type: KType,
			configure: RaptorScalarGraphDefinitionBuilder<Type>.() -> Unit,
		) {
			nestedDefinitions += RaptorScalarGraphDefinitionBuilder<Type>(
				kotlinType = KotlinType.of(type, requireSpecialization = false, allowMaybe = false, allowNull = false),
				name = RaptorGraphDefinition.resolveName(
					name,
					defaultNamePrefix = this@RaptorStructuredGraphTypeDefinitionBuilder.name,
					type = type
				),
				stackTrace = stackTrace(skipCount = 1)
			)
				.apply(configure)
		}
	}
}
