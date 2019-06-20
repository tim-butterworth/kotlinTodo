import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "todo-runner"
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

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    implementation(project(":adapters"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.31")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.31")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.3.31")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spec_version")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spec_version")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.13")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.0")

    implementation("com.github.docker-java:docker-java:3.1.2")

    implementation("org.json:json:20180813")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(Pair("Main-Class", "tim.todos.runner.RunnerKt"))
        )
    }
    archiveFileName.set("todoer.jar")
    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) {
                it
            } else zipTree(it)
        }
        , {
            exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        }
    )
}