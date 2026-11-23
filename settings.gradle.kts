pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://maven.architectury.dev") { name = "Architectury" }
        maven("https://maven.minecraftforge.net") { name = "MinecraftForge" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.firstdark.dev/releases") { name = "ModPublisher Release" }
    }
}

plugins {
    // Check the latest version on https://stonecutter.kikugie.dev/blog/changes/0.9
    id("dev.kikugie.stonecutter") version "0.9.8"

    // Cross-compat for 26.1+ and older versions (https://codeberg.org/KikuGie/loom-back-compat)
    id("dev.kikugie.loom-back-compat") version "0.4.2"

    // Toolchain auto-provisioning (https://github.com/gradle/foojay-toolchains)
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        /**
         * Creates a node per loader, named `versions/{project}-{loader}`,
         * with its own build script `build.{loader}.gradle.kts`.
         */
        fun match(project: String, vararg loaders: String, version: String = project) {
            for (loader in loaders) {
                version("$project-$loader", version).buildscript("$loader/build.gradle.kts")
            }
        }

        // Legacy Forge (1.19.2 / 1.20.1) is provided by `net.neoforged.moddev.legacyforge`,
        // NeoForge (1.21.1+) by `net.neoforged.moddev`.
        match("1.19.2", "fabric", "forge")
        match("1.20.1", "fabric", "forge")
        match("1.21.1", "fabric", "neoforge")
        match("26.1.2", "fabric", "neoforge")

        vcsVersion = "1.20.1-fabric"
    }
}

rootProject.name = "OpenInInventory"
