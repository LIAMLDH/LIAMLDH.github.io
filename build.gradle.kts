plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

tasks.register<JavaExec>("wrapper") {
    group = "build"
    description = "Generate Gradle wrapper"
    classpath = files("gradle/wrapper/gradle-wrapper.jar")
    mainClass.set("org.gradle.wrapper.GradleWrapperMain")
    args("--gradle-version=8.14.4")
    jvmArgs = listOf("-Xmx64m", "-Xms64m")
}
