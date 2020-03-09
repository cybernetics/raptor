package io.fluidsonic.raptor

import io.fluidsonic.graphql.*
import org.bson.*
import org.bson.types.*
import kotlin.reflect.*


interface EntityId {

	val factory: Factory<*>


	interface Factory<Id : EntityId> {

		fun bsonDefinition() = bsonDefinition(idClass) {
			decode {
				readIdValue()
			}

			encode { value ->
				writeIdValue(value)
			}
		}


		fun graphDefinition() = graphAliasDefinition {
			conversion(idClass, GraphId::class) {
				parse { parse(it.value) ?: throw GError("\"${it.value}\" is not a valid '$graphName'.") }
				serialize { GraphId(it.serialize()) }
			}
		}

		val graphName: String

		val idClass: KClass<Id>

		val type: String

		fun parse(string: String): Id?

		fun parseWithoutType(string: String): Id?

		fun BsonReader.readIdValue(): Id

		fun BsonWriter.writeIdValue(id: Id)

		fun Id.serialize(): String

		fun Id.serializeWithoutType(): String
	}


	interface ObjectIdBased : EntityId {

		val value: ObjectId


		abstract class Factory<Id : ObjectIdBased>(
			final override val type: String, // FIXME confusing - rename - also, check for duplicates
			final override val idClass: KClass<Id>,
			final override val graphName: String = idClass.simpleName!!, // FIXME improve
			private val constructor: (raw: ObjectId) -> Id
		) : EntityId.Factory<Id> {

			private val prefix = "$type/"


			final override fun parse(string: String) =
				string
					.takeIf { it.startsWith(prefix) || !it.contains('/') }
					?.removePrefix(prefix)
					?.let { parseWithoutType(it) }


			final override fun parseWithoutType(string: String) =
				string
					.let {
						try {
							ObjectId(it)
						}
						catch (_: Exception) {
							null
						}
					}
					?.let(constructor)


			final override fun BsonReader.readIdValue() =
				constructor(readObjectId())


			final override fun BsonWriter.writeIdValue(id: Id) =
				writeObjectId(id.value)


			final override fun Id.serialize() =
				prefix + serializeWithoutType()


			override fun Id.serializeWithoutType() =
				value.toHexString()!!
		}
	}


	interface StringBased : EntityId {

		val value: String


		abstract class Factory<Id : StringBased>(
			final override val type: String,
			final override val idClass: KClass<Id>,
			final override val graphName: String = idClass.simpleName!!, // FIXME improve
			private val constructor: (raw: String) -> Id
		) : EntityId.Factory<Id> {

			private val prefix = "$type/"


			final override fun parse(string: String) =
				string
					.takeIf { it.startsWith(prefix) || !it.contains('/') }
					?.removePrefix(prefix)
					?.let { parseWithoutType(it) }


			final override fun parseWithoutType(string: String) =
				constructor(string)


			final override fun BsonReader.readIdValue() =
				constructor(readString())


			final override fun BsonWriter.writeIdValue(id: Id) =
				writeString(id.value)


			final override fun Id.serialize() =
				prefix + serializeWithoutType()


			override fun Id.serializeWithoutType() =
				value
		}
	}
}


@Suppress("UNCHECKED_CAST")
fun EntityId.toStringWithoutType() =
	(factory as EntityId.Factory<EntityId>).run { serializeWithoutType() }


val EntityId.typed
	get() = TypedId(this)


fun <Id : EntityId> EntityId.Factory<Id>.serialize(id: Id) =
	id.run { serialize() }
