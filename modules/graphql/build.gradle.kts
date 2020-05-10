import io.fluidsonic.gradle.*

fluidJvmLibraryVariant(JvmTarget.jdk8) {
	description = "FIXME"
}

dependencies {
	api(project(":raptor-ktor"))
	implementation(fluid("graphql", "0.9.0"))
	implementation(fluid("json-basic", "1.0.3"))
	implementation(fluid("stdlib", "0.9.31"))
	implementation(fluid("time", "0.9.19"))
}
