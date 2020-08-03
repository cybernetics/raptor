package io.fluidsonic.raptor.graphql.internal

import io.fluidsonic.graphql.*
import io.fluidsonic.raptor.*
import io.fluidsonic.stdlib.*
import kotlin.reflect.*


internal class GraphSystemBuilder private constructor(
	private val typeSystem: GraphTypeSystem,
) {

	private val interfaceTypesByKotlinType = typeSystem.types
		.filterIsInstance<InterfaceGraphType>()
		.associateBy { it.kotlinType }


	private fun build() = GraphSystem(
		schema = buildSchema(),
		typeSystem = typeSystem
	)


	private fun buildSchema() = GSchema(GDocument(
		definitions = buildDirectiveDefinitions() + buildTypeDefinitions()
	))


	@OptIn(ExperimentalStdlibApi::class)
	private fun buildDirectiveDefinitions(): List<GDirectiveDefinition> = buildList {
		val referencedDirectiveNames = findReferencedDirectiveNames()

		if (referencedDirectiveNames.contains(GraphDirective.optional.name))
			add(GDirectiveDefinition(
				description = "An argument with this directive does not require a value. " +
					"Providing no value may lead to a different behavior than providing a null value.",
				name = GraphDirective.optional.name,
				locations = setOf(GDirectiveLocation.ARGUMENT_DEFINITION)
			))
	}


	private fun buildEnumDefinition(type: EnumGraphType) = GEnumType(
		description = type.description,
		name = type.name,
		values = type.values.map { GEnumValueDefinition(name = it) }.sortedBy { it.name },
		extensions = GNodeExtensionSet {
			outputCoercer = EnumCoercer
			nodeInputCoercer = EnumCoercer
			raptorType = type
			variableInputCoercer = EnumCoercer
		}
	)


	private fun buildFieldArgumentDefinition(argument: GraphArgument) = GFieldArgumentDefinition(
		defaultValue = argument.defaultValue,
		description = argument.description,
		directives = argument.directives.map { GDirective(name = it.name) }.sortedBy { it.name },
		name = argument.name,
		type = typeRef(argument.kotlinType, isInput = true),
		extensions = GNodeExtensionSet {
			raptorArgument = argument
			raptorType = underlyingType(argument.kotlinType, isInput = true)
		}
	)


	private fun buildFieldDefinition(field: GraphField) = GFieldDefinition(
		argumentDefinitions = field.arguments.map(::buildFieldArgumentDefinition).sortedBy { it.name },
		description = field.description,
		name = field.name,
		type = typeRef(field.kotlinType, isInput = false),
		extensions = GNodeExtensionSet {
			raptorField = field
			raptorType = underlyingType(field.kotlinType, isInput = false)
			resolver = FieldResolver
		}
	)


	private fun buildInputObjectArgumentDefinition(argument: GraphArgument) = GInputObjectArgumentDefinition(
		defaultValue = argument.defaultValue,
		description = argument.description,
		directives = argument.directives.map { GDirective(name = it.name) }.sortedBy { it.name },
		name = argument.name,
		type = typeRef(argument.kotlinType, isInput = true),
		extensions = GNodeExtensionSet {
			raptorArgument = argument
			raptorType = underlyingType(argument.kotlinType, isInput = true)
		}
	)


	private fun buildInputObjectDefinition(type: InputObjectGraphType) = GInputObjectType(
		argumentDefinitions = type.arguments.map(::buildInputObjectArgumentDefinition).sortedBy { it.name },
		description = type.description,
		name = type.name,
		extensions = GNodeExtensionSet {
			nodeInputCoercer = InputObjectCoercer
			raptorType = type
			variableInputCoercer = InputObjectCoercer
		}
	)


	private fun buildInterfaceDefinition(type: InterfaceGraphType) = GInterfaceType(
		description = type.description,
		fieldDefinitions = type.fields.map(::buildFieldDefinition).sortedBy { it.name },
		name = type.name,
		extensions = GNodeExtensionSet {
			raptorType = type
		}
	)


	private fun buildObjectDefinition(type: ObjectGraphType) = GObjectType(
		description = type.description,
		fieldDefinitions = type.fields.map(::buildFieldDefinition).sortedBy { it.name },
		interfaces = interfaceTypeRefsForKotlinType(type.kotlinType).sortedBy { it.name },
		name = type.name,
		extensions = GNodeExtensionSet {
			kotlinType = type.kotlinType.classifier
			raptorType = type
		}
	)


	private fun buildScalarDefinition(type: ScalarGraphType) = GCustomScalarType(
		description = type.description,
		name = type.name,
		extensions = GNodeExtensionSet {
			nodeInputCoercer = ScalarCoercer
			outputCoercer = ScalarCoercer
			raptorType = type
			variableInputCoercer = ScalarCoercer
		}
	)


	private fun buildTypeDefinition(type: NamedGraphType) = when (type) {
		is EnumGraphType -> buildEnumDefinition(type)
		is InputObjectGraphType -> buildInputObjectDefinition(type)
		is InterfaceGraphType -> buildInterfaceDefinition(type)
		is ObjectGraphType -> buildObjectDefinition(type)
		is ScalarGraphType -> buildScalarDefinition(type)
	}


	private fun buildTypeDefinitions(): List<GNamedType> =
		typeSystem.types.filterIsInstance<NamedGraphType>().map(::buildTypeDefinition).sortedBy { it.name }


	@OptIn(ExperimentalStdlibApi::class)
	private fun findReferencedDirectiveNames(): Set<String> =
		typeSystem.types.flatMapTo(hashSetOf()) { type ->
			when (type) {
				is AliasGraphType,
				is EnumGraphType,
				is ScalarGraphType,
				->
					emptyList()

				is InputObjectGraphType ->
					type.arguments.flatMap { it.directives }.map { it.name }

				is InterfaceGraphType ->
					type.fields.flatMap { it.arguments }.flatMap { it.directives }.map { it.name }

				is ObjectGraphType ->
					type.fields.flatMap { it.arguments }.flatMap { it.directives }.map { it.name }
			}
		}


	private fun interfaceTypeRefsForKotlinType(kotlinType: KotlinType): List<GNamedTypeRef> {
		val typeNames = mutableSetOf<String>()
		interfaceTypeNamesForObjectValueClass(kotlinType, target = typeNames)

		return typeNames.map(::GNamedTypeRef)
	}


	// FIXME Won't work for generic interfaces
	private fun interfaceTypeNamesForObjectValueClass(kotlinType: KotlinType, target: MutableSet<String>) {
		for (superType in kotlinType.classifier.supertypes) {
			val superClass = superType.classifier as? KClass<*> ?: continue
			val superKotlinType = KotlinType(classifier = superClass)

			val gqlSuperClassName = interfaceTypesByKotlinType[superKotlinType]?.name
			if (gqlSuperClassName !== null)
				target += gqlSuperClassName

			interfaceTypeNamesForObjectValueClass(superKotlinType, target = target)
		}
	}


	private fun underlyingType(kotlinType: KotlinType, isInput: Boolean): GraphType =
		when (kotlinType.classifier) {
			Collection::class, List::class, Maybe::class, Set::class -> // FIXME improve
				underlyingType(checkNotNull(kotlinType.typeArgument), isInput = isInput)

			else -> when (isInput) {
				true -> typeSystem.resolveInputType(kotlinType)
				false -> typeSystem.resolveOutputType(kotlinType)
			} ?: error("Cannot resolve GraphQL type for Kotlin type '$kotlinType'.")
		}


	private fun typeRef(kotlinType: KotlinType, isInput: Boolean): GTypeRef =
		when (kotlinType.classifier) {
			Collection::class, List::class, Maybe::class, Set::class -> // FIXME improve
				typeRef(checkNotNull(kotlinType.typeArgument), isInput = isInput)

			else -> when (isInput) {
				true -> typeSystem.resolveInputType(kotlinType)
				false -> typeSystem.resolveOutputType(kotlinType)
			}
				.ifNull { error("Cannot resolve GraphQL type for Kotlin type '$kotlinType'.") }
				.let { type ->
					when (type) {
						is AliasGraphType -> when {
							type.isId -> GIdTypeRef
							else -> typeRef(type.referencedKotlinType, isInput = isInput)
						}

						is NamedGraphType ->
							GNamedTypeRef(type.name)
					}
				}
		}


	companion object {

		fun build(typeSystem: GraphTypeSystem): GraphSystem =
			GraphSystemBuilder(typeSystem = typeSystem).build()
	}
}
