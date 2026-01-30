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
    if (stonecutter.current.project !in it) {
        null
    } else {
        it.project.prop("loom.platform")
    }
})

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modApi("dev.architectury:architectury:${mod.dep("arch-api")}") {
        this.isTransitive = false
    }

    compileOnly("com.blamejared.crafttweaker:CraftTweaker-common-${minecraft}:${mod.dep("crafttweaker")}")

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

allprojects {
    java {
        withSourcesJar()
        val java = if (stonecutter.eval(minecraft, ">=1.20.5")) {
            JavaVersion.VERSION_21
        } else if (stonecutter.eval(minecraft, ">=1.18")) {
            JavaVersion.VERSION_17
        } else if (stonecutter.eval(minecraft, ">=1.17")) {
            JavaVersion.VERSION_16
        } else {
            JavaVersion.VERSION_1_8
        }
        targetCompatibility = java
        sourceCompatibility = java
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
}
