package io.fluidsonic.raptor

import io.fluidsonic.country.*
import io.fluidsonic.currency.*
import io.fluidsonic.locale.*
import io.fluidsonic.time.*
import io.ktor.http.*
import kotlinx.datetime.*


public object RaptorGraphDefaults {

	public val definitions: Collection<RaptorGraphDefinition> = listOf<RaptorGraphDefinition>(
		Country.graphDefinition(),
		CountryCode.graphDefinition(),
		Currency.graphDefinition(),
//		Duration.graphDefinition(), // FIXME
		LocalDate.graphDefinition(),
		LocalDateTime.graphDefinition(),
		LocalTime.graphDefinition(),
		Locale.graphDefinition(),
		Timestamp.graphDefinitions(),
		TimeZone.graphDefinition(),
		Unit.graphDefinition(),
		Url.graphDefinition()
	)
}
