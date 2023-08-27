import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "chat"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val voyagerVersion = "1.0.0-rc05"
        val kamelVersion = "0.7.1"
        val iconsVersion = "1.1.0"
        val ktorVersion = "2.3.0"
        val okioVersion = "3.5.0"
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                //implementation("media.kamel:kamel-image:$kamelVersion")
                implementation("br.com.devsrsouza.compose.icons:font-awesome:$iconsVersion")
                implementation("com.squareup.okio:okio:$okioVersion")

                implementation("io.ktor:ktor-client-cio:$ktorVersion")

                // serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // arrow
                implementation("io.arrow-kt:arrow-core:1.2.0")
                implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")
                implementation("io.arrow-kt:arrow-fx-stm:1.2.0")
                implementation("io.arrow-kt:arrow-resilience:1.2.0")
            }
        }
        val jvmTest by getting
    }
}

dependencies {

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
