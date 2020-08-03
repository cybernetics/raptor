package io.fluidsonic.raptor

import io.fluidsonic.graphql.*
import io.fluidsonic.raptor.graphql.internal.*


internal class GraphSystem(
	val schema: GSchema,
	val typeSystem: GraphTypeSystem,
) {

	private val executor = GExecutor.default(
		schema = schema,
		nodeInputCoercer = NodeInputCoercer,
		variableInputCoercer = VariableInputCoercer
	)


	suspend fun execute(
		documentSource: GDocumentSource.Parsable,
		operationName: String? = null,
		variableValues: Map<String, Any?> = emptyMap(),
		context: RaptorGraphContext,
	): Map<String, Any?> =
		executor.serializeResult(executor.execute(
			documentSource = documentSource,
			operationName = operationName,
			variableValues = variableValues,
			extensions = GExecutorContextExtensionSet {
				raptorContext = context
			}
		))
}