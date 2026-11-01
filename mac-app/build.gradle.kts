import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.3"
}

val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

kotlin {
    jvmToolchain(17)
}

// Optional TMDB key from the GitHub secret TMDB_API_KEY (can also be typed in Settings).
val keysDir = layout.buildDirectory.dir("generated/keys")
val generateKeys by tasks.registering {
    val key = System.getenv("TMDB_API_KEY").orEmpty()
    inputs.property("tmdb", key)
    outputs.dir(keysDir)
    doLast {
        val f = keysDir.get().file("com/lostar/tv/desktop/BuildKeys.kt").asFile
        f.parentFile.mkdirs()
        f.writeText("package com.lostar.tv.desktop\n\ninternal object BuildKeys {\n    const val TMDB = \"$key\"\n}\n")
    }
}
tasks.named("compileKotlin") { dependsOn(generateKeys) }

// The playlist parsers, duplicate merging and link-health engine are shared with the Android TV app.
sourceSets {
    main {
        kotlin.srcDir("../tv-app/app/src/main/java")
        kotlin.srcDir(keysDir)
        kotlin.include(
            "com/lostar/tv/desktop/**",
            "com/lostar/tv/data/**",
            "com/lostar/tv/ui/Format.kt",
        )
        kotlin.exclude("**/UserStore.kt")
    }
    test {
        kotlin.srcDir("../tv-app/app/src/test/java")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("uk.co.caprica:vlcj:4.7.3")
    testImplementation("junit:junit:4.13.2")
}

compose.desktop {
    application {
        mainClass = "com.lostar.tv.desktop.MainKt"
        jvmArgs += listOf("-Xmx2g")
        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "LostTV"
            packageVersion = "1.0.$runNumber"
            description = "LostTV — lettore per la tua lista IPTV"
            vendor = "Lostar"
            modules("java.instrument", "java.management", "java.naming", "java.net.http", "java.sql", "jdk.unsupported", "jdk.crypto.ec")
            macOS {
                bundleID = "com.lostar.losttv"
                dockName = "LostTV"
            }
        }
    }
}
