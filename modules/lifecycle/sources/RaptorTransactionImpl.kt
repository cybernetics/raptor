package io.fluidsonic.raptor


internal class RaptorTransactionImpl(
	parentContext: RaptorContextImpl
) : RaptorTransaction {

	override val context = RaptorTransactionContextImpl(
		parentContext = parentContext
	)
}