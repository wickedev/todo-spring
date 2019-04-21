import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Versions {
    const val kotlin = "1.3.30"
    const val coroutine = "1.2.0"
    const val spek = "2.0.2"
    const val expekt = "0.5.0"
    const val netty = "4.1.35.Final"
    const val spring = "5.1.6.RELEASE"
}

plugins {
    kotlin("jvm") version "1.3.30"
    java
    application
}

group = "dev.wicke"
version = "0.1.0"

application {
    mainClassName = "dev.wicke.todo.MainKt"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation("io.netty:netty-all:${Versions.netty}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}")
    implementation("org.springframework:spring-webflux:${Versions.spring}")
    implementation("org.springframework:spring-context:${Versions.spring}")
    implementation("io.projectreactor.netty:reactor-netty:0.8.6.RELEASE")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.2.0")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spek}")
    testImplementation("com.winterbe:expekt:${Versions.expekt}")

    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


// setup the test task
tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines.add("spek2")
    }
}
