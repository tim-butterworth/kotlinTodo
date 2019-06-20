import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "adapters"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/spekframework/spek/")
    }
}

val arrow_version = "0.8.2"
val spec_version = "2.0.2"
val jackson_version = "2.9.8"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))

    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.21")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spec_version")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spec_version")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.13")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}