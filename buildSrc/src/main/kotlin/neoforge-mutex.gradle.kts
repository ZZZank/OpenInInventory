import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

// Prevents NeoForge/LegacyForge nodes from recompiling Minecraft in parallel,
// which would otherwise saturate the machine on multi-version builds.
interface MinecraftArtifactsMutex : BuildService<BuildServiceParameters.None>

val mutex = gradle.sharedServices.registerIfAbsent("createMinecraftArtifactsMutex", MinecraftArtifactsMutex::class.java) {
    maxParallelUsages.set(1)
}

tasks.matching { it.name == "createMinecraftArtifacts" }.configureEach {
    usesService(mutex)
}
