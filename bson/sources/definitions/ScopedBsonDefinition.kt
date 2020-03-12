package io.fluidsonic.raptor

import kotlin.reflect.*


private class ScopedBsonDefinition<Value : Any>(
	private val config: RaptorBsonDefinitionScope<Value>.() -> Unit,
	private val valueClass: KClass<Value>
) : RaptorBsonDefinition<Value> {

	override fun codec(scope: BsonScope) =
		BsonDefinitionScopeImpl(scope = scope, valueClass = valueClass).apply(config).codec()
}


// FIXME add @BuilderInference once compiler errors are fixed
inline fun <reified Value : Any> bsonDefinition(noinline config: RaptorBsonDefinitionScope<Value>.() -> Unit) =
	bsonDefinition(valueClass = Value::class, config = config)


fun <Value : Any> bsonDefinition(valueClass: KClass<Value>, config: RaptorBsonDefinitionScope<Value>.() -> Unit): RaptorBsonDefinition<Value> =
	ScopedBsonDefinition(config = config, valueClass = valueClass)