package io.fluidsonic.raptor

import io.fluidsonic.time.*
import kotlin.contracts.*
import org.bson.*
import org.bson.types.*


public interface RaptorBsonWriter {

	@RaptorDsl
	public fun endArray()

	@RaptorDsl
	public fun endDocument()

	@RaptorDsl
	public fun fieldName(name: String)

	@InternalRaptorApi
	@RaptorDsl
	public fun internal(): BsonWriter

	@RaptorDsl
	public fun startArray()

	@RaptorDsl
	public fun startDocument()

	@RaptorDsl
	public fun value(value: Any?)

	@RaptorDsl
	public fun value(value: Boolean)

	@RaptorDsl
	public fun value(value: Double)

	@RaptorDsl
	public fun value(value: Float)

	@RaptorDsl
	public fun value(value: Int)

	@RaptorDsl
	public fun value(value: Long)

	@RaptorDsl
	public fun value(value: Nothing?)

	@RaptorDsl
	public fun value(value: ObjectId)

	@RaptorDsl
	public fun value(value: Short)

	@RaptorDsl
	public fun value(value: String)

	@RaptorDsl
	public fun value(value: Timestamp)
}


@RaptorDsl
public inline fun <Writer : RaptorBsonWriter> Writer.array(field: String, write: Writer.() -> Unit) {
	contract {
		callsInPlace(write, InvocationKind.EXACTLY_ONCE)
	}

	fieldName(field)
	array(write)
}


@RaptorDsl
public inline fun <Writer : RaptorBsonWriter> Writer.array(write: Writer.() -> Unit) {
	contract {
		callsInPlace(write, InvocationKind.EXACTLY_ONCE)
	}

	startArray()
	write()
	endArray()
}


@RaptorDsl
public inline fun <Writer : RaptorBsonWriter> Writer.document(field: String, write: Writer.() -> Unit) {
	contract {
		callsInPlace(write, InvocationKind.EXACTLY_ONCE)
	}

	fieldName(field)
	document(write)
}


@RaptorDsl
public inline fun <Writer : RaptorBsonWriter> Writer.document(write: Writer.() -> Unit) {
	contract {
		callsInPlace(write, InvocationKind.EXACTLY_ONCE)
	}

	startDocument()
	write()
	endDocument()
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Any?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Boolean) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Boolean?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Double) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Double?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Float) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Float?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Int) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Int?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Long) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Long?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: ObjectId?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Short) {
	fieldName(field)
	value(value)
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Short?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: String?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}


@RaptorDsl
public fun RaptorBsonWriter.value(field: String, value: Timestamp?, preserveNull: Boolean = false) {
	if (value == null && !preserveNull)
		return

	fieldName(field)

	when (value) {
		null -> value(null)
		else -> value(value)
	}
}
