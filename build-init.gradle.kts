import java.util.*

val javaHome = System.getProperty("java.home")
println("Current JAVA_HOME: $javaHome")

val version = Runtime.version().toString()
println("Java Version: $version")

// For Gradle builds with Java 25, we need to use proper toolchain setup
