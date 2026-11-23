plugins {
    id("dev.kikugie.loom-back-compat")
    id("com.hypherionmc.modutils.modpublisher") version "2.2.3"
    `repo-convention`
}

// Structured properties are exposed as Gradle properties, so `-Pkey=value` overrides them
fun prop(key: String): Any? = project.property(key)

// DO NOT set group = ...!
version = "${prop("mod.version")}+${sc.current.version}"
base.archivesName.set("${prop("mod.id")}-${prop("loom.platform")}")

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

// Shared `src/` is linked in by Stonecutter; loader-specific sources are layered on top
sourceSets {
    main {
        // NOTE: use `file(...)` directly to point to generated sources
        java.srcDir(file("${prop("loom.platform")}/src/main/java"))
        resources.srcDir(file("${prop("loom.platform")}/src/main/resources"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")
    modImplementation("dev.architectury:architectury-fabric:${prop("deps.arch")}") {
        isTransitive = false
    }

    // KubeJS is NeoForge-only after 1.21
    if (sc.current.parsed < "1.21") {
        modCompileOnly("dev.latvian.mods:kubejs:${prop("deps.kubejs")}") {
            isTransitive = false
        }
        // Rhino is only here to satisfy KubeJS' public signatures
        modCompileOnly("dev.latvian.mods:rhino:${prop("deps.rhino")}")
    }

    // CraftTweaker has no 26.x line yet; its integration is compiled out on those nodes
    if (sc.current.parsed < "26.1") {
        compileOnly("com.blamejared.crafttweaker:CraftTweaker-common-${sc.current.version}:${prop("deps.crafttweaker")}") {
            isTransitive = false
        }
    }

    compileOnly("com.demonwav.mcdev:annotations:2.1.0")

    // for testing
    if (sc.current.version == "1.20.1") {
        // Scannable: open with Shift + Right click
        modRuntimeOnly("curse.maven:forge-config-api-port-547434:7260491")
        modRuntimeOnly("teamreborn:energy:3.0.0")
        modRuntimeOnly("curse.maven:scannable-266784:4763071")
        // ModernFix: optimization
        modRuntimeOnly("curse.maven:modernfix-790626:7306748")
        // Patchouli: open with Right click
        modRuntimeOnly("vazkii.patchouli:Patchouli:1.20.1-84.1-FABRIC")
    }
}

loom {
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run") // Shares the run directory between versions
        jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        jvmArguments.add("-Xmx2g")
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        // Few developers ship source jars, so keep parameter names for those who need them
        options.compilerArgs.add("-parameters")
    }

    processResources {
        val props = buildMap {
            set("id", prop("mod.id") as String)
            set("name", prop("mod.name") as String)
            set("version", prop("mod.version") as String)
            set("authors", prop("mod.authors") as String)
            set("minecraft", prop("mod.mc_dep_fabric") as String)
        }
        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}`"

        inputs.property("version", prop("mod.version"))
        // loomx.mod(Sources)Jar resolves the jar task of the active Loom variant
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs/${prop("mod.version")}"))
    }
}

// see: https://github.com/firstdarkdev/modpublisher
publisher {
    validatedProp("publish.modrinth", "MODRINTH_TOKEN") { id, key ->
        modrinthID.set(id)
        apiKeys { modrinth(key) }
    }

    validatedProp("publish.curseforge", "CURSE_TOKEN") { id, key ->
        curseID.set(id)
        apiKeys { curseforge(key) }
    }

    validatedProp("publish.github", "GITHUB_TOKEN") { id, key ->
        githubRepo.set(id)
        apiKeys { github(key) }
    }

    val publishType = prop("publish.type") as String
    if (publishType == "debug") {
        // No files will actually be uploaded
        debug.set(true)
    } else {
        versionType.set(publishType)
    }

    val modVersion = prop("mod.version") as String
    val mcVersionTitle = prop("mod.mc_title") as String
    val platform = prop("loom.platform") as String

    changelog.set(rootProject.file("CHANGELOG.md"))
    projectVersion.set(modVersion)
    // Example: 1.2.3 for 1.20.1 fabric
    displayName.set("$modVersion for $mcVersionTitle $platform")
    gameVersions.set((prop("mod.mc_targets") as String).split(" "))
    loaders.set(listOf(platform))
    artifact.set(loomx.modJar)
}

fun validatedProp(key: String, env: String, action: (id: String, apiKey: String) -> Unit) {
    val projectID = prop(key) as String?
    val apiKey = System.getenv(env)
    if (projectID != null && !projectID.startsWith('[') && apiKey != null && apiKey.isNotEmpty()) {
        action(projectID, apiKey)
    }
}
