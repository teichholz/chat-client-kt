import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //kotlin("multiplatform")
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("io.kotest.multiplatform") version "5.0.2"
    id("idea")
}

group = "chat"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val voyagerVersion = "1.0.0-rc06"
val kamelVersion = "0.7.1"
val iconsVersion = "1.1.0"
val ktorVersion = "2.3.0"
val okioVersion = "3.5.0"
val logbackVersion = "1.4.7"
val slf4jVersion = "2.0.7"
dependencies {
    implementation(compose.desktop.macos_arm64)
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("media.kamel:kamel-image:$kamelVersion")
    implementation("br.com.devsrsouza.compose.icons:font-awesome:$iconsVersion")
    implementation("com.squareup.okio:okio:$okioVersion")

    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-resources:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
    // arrow
    implementation("io.arrow-kt:arrow-core:1.2.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")
    implementation("io.arrow-kt:arrow-fx-stm:1.2.0")
    implementation("io.arrow-kt:arrow-resilience:1.2.0")

    // logging
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")

    // commons
    implementation("chat.commons:chat-commons:1.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "chat-client"
            packageVersion = "1.0.0"
        }
    }
}
