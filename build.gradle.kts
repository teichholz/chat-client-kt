import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
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

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val voyagerVersion = "1.0.0-rc06"
        val kamelVersion = "0.7.1"
        val iconsVersion = "1.1.0"
        val ktorVersion = "2.3.0"
        val okioVersion = "3.5.0"
        val logbackVersion = "1.4.7"
        val slf4jVersion = "2.0.7"
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("media.kamel:kamel-image:$kamelVersion")
                implementation("br.com.devsrsouza.compose.icons:font-awesome:$iconsVersion")
                implementation("com.squareup.okio:okio:$okioVersion")

                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-resources:$ktorVersion")

                // serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

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
                implementation("chat.commons:chat-commons:1.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.6.0")
                implementation("io.kotest:kotest-assertions-core:5.6.0")
                implementation("io.kotest:kotest-property:5.6.0")
            }
        }
    }
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
