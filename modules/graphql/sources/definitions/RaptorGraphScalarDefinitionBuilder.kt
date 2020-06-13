package io.fluidsonic.raptor

import kotlin.reflect.*


@RaptorDsl
class RaptorGraphScalarDefinitionBuilder<Value : Any> internal constructor(
	name: String,
	private val stackTrace: List<StackTraceElement>,
	valueClass: KClass<Value>
) : RaptorGraphNamedTypeDefinitionBuilder<Value, GraphScalarDefinition<Value>>(
	name = name,
	valueClass = valueClass
) {

	private var jsonInputClass: KClass<*>? = null
	private var parseBoolean: (RaptorGraphScope.(input: Boolean) -> Value?)? = null
	private var parseFloat: (RaptorGraphScope.(input: Double) -> Value?)? = null
	private var parseObject: (RaptorGraphScope.(input: Map<String, *>) -> Value?)? = null
	private var parseInt: (RaptorGraphScope.(input: Int) -> Value?)? = null
	private var parseJson: (RaptorGraphScope.(input: Any) -> Value?)? = null
	private var parseString: (RaptorGraphScope.(input: String) -> Value?)? = null
	private var serializeJson: (RaptorGraphScope.(value: Value) -> Any)? = null


	override fun build(description: String?): GraphScalarDefinition<Value> {
		checkNotNull(parseBoolean ?: parseFloat ?: parseInt ?: parseObject ?: parseString) {
			"At least one GraphQL value parsing function must be defined: parseBoolean/Int/Float/String { … }"
		}

		return GraphScalarDefinition(
			description = description,
			jsonInputClass = checkNotNull(jsonInputClass),
			name = name,
			parseBoolean = parseBoolean,
			parseFloat = parseFloat,
			parseObject = parseObject,
			parseInt = parseInt,
			parseJson = parseJson
				?: error("JSON parsing must be defined: parseJson { … }"),
			parseString = parseString,
			serializeJson = serializeJson
				?: error("JSON serializing must be defined: serializeJson { … }"),
			stackTrace = stackTrace,
			valueClass = valueClass
		)
	}


	@RaptorDsl
	fun parseBoolean(parse: KFunction1<Boolean, Value?>) =
		parseBoolean { input ->
			parse(input)
		}


	@RaptorDsl
	fun parseBoolean(parse: RaptorGraphScope.(input: Boolean) -> Value?) {
		check(parseBoolean === null) { "Cannot define multiple GraphQL Boolean parsers." }

		@Suppress("UNCHECKED_CAST")
		parseBoolean = parse
	}


	@RaptorDsl
	fun parseFloat(parse: KFunction1<Double, Value?>) =
		parseFloat { input ->
			parse(input)
		}


	@RaptorDsl
	fun parseFloat(parse: RaptorGraphScope.(input: Double) -> Value?) {
		check(parseFloat === null) { "Cannot define multiple GraphQL Float parsers." }

		@Suppress("UNCHECKED_CAST")
		parseFloat = parse
	}


	@RaptorDsl
	fun parseInt(parse: KFunction1<Int, Value?>) =
		parseInt { input ->
			parse(input)
		}


	@RaptorDsl
	fun parseInt(parse: RaptorGraphScope.(input: Int) -> Value?) {
		check(parseInt === null) { "Cannot define multiple GraphQL Int parsers." }

		@Suppress("UNCHECKED_CAST")
		parseInt = parse
	}


	// FIXME standardize
	@RaptorDsl
	inline fun <reified JsonInput : Any> parseJson(parse: KFunction1<JsonInput, Value?>) =
		parseJson(jsonInputClass = JsonInput::class) { input ->
			parse(input)
		}


	@RaptorDsl
	inline fun <reified JsonInput : Any> parseJson(noinline parse: RaptorGraphScope.(input: JsonInput) -> Value?) =
		parseJson(jsonInputClass = JsonInput::class, parse = parse)


	@RaptorDsl
	fun <JsonInput : Any> parseJson(jsonInputClass: KClass<out JsonInput>, parse: RaptorGraphScope.(input: JsonInput) -> Value?) {
		check(parseJson === null) { "Cannot define multiple JSON parsers." }

		this.jsonInputClass = jsonInputClass

		@Suppress("UNCHECKED_CAST")
		parseJson = parse as RaptorGraphScope.(input: Any) -> Value?
	}


	@RaptorDsl
	fun parseObject(parse: RaptorGraphScope.(input: Map<String, *>) -> Value?) {
		check(parseObject === null) { "Cannot define multiple GraphQL Object parsers." }

		parseObject = parse
	}


	@RaptorDsl
	fun parseString(parse: KFunction1<String, Value?>) {
		parseString { input ->
			parse(input)
		}
	}


	@RaptorDsl
	fun parseString(parse: RaptorGraphScope.(input: String) -> Value?) {
		check(parseString === null) { "Cannot define multiple GraphQL String parsers." }

		parseString = parse
	}


	@RaptorDsl
	fun serializeJson(serialize: KFunction1<Value, Any>) {
		serializeJson { value ->
			serialize(value)
		}
	}


	@RaptorDsl
	fun serializeJson(serialize: KProperty1<Value, Any>) {
		serializeJson { value ->
			serialize(value)
		}
	}


	@RaptorDsl
	fun serializeJson(serialize: RaptorGraphScope.(value: Value) -> Any) {
		check(serializeJson === null) { "Cannot define multiple JSON serializers." }

		serializeJson = serialize
	}
}
