import net.fabricmc.loom.task.RunGameTask

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    `repo-convention`
}

val minecraft = stonecutter.current.version

version = "${mod.version}+$minecraft"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project in it) {
        it.project.prop("loom.platform")
    } else {
        null
    }
})

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modApi("dev.architectury:architectury:${mod.dep("arch-api")}") {
        this.isTransitive = false
    }

    compileOnly("com.blamejared.crafttweaker:CraftTweaker-common-${minecraft}:${mod.dep("crafttweaker")}") {
        isTransitive = false
    }

    // Source: https://mvnrepository.com/artifact/com.demonwav.mcdev/annotations
    compileOnly("com.demonwav.mcdev:annotations:2.1.0")
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

stonecutter {
    replacements.string(eval(minecraft, "<1.20")) {
        replace("net.minecraft.registry.Registries", "net.minecraft.util.registry.Registry")
        replace("Registries.", "Registry.")
    }
}

java {
    withSourcesJar()

    val requiredJava = when {
        sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
        sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
        sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
        sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
        else -> JavaVersion.VERSION_1_8
    }

    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.compileJava {
    options.encoding = "UTF-8"

    // very few developers will provide source jar when publishing mods, we add param names in production jar
    // to make life easier for those who need to work with the mod
    options.compilerArgs.add("-parameters")
}

tasks.withType<RunGameTask>().configureEach {
    this.jvmArgs("-Xmx2048m")
}
