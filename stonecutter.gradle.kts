plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1-fabric" /* [SC] DO NOT EDIT */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Applies version- and loader-specific properties from `stonecutter.properties.toml`
    properties {
        tags(version, loader)
    }

    // Adds constants for Stonecutter comments (i.e. `//? if fabric { ... }`)
    constants {
        match(loader, "fabric", "forge", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
}

// Runs the client/server of whichever node is currently active
for (type in listOf("Client", "Server")) {
    tasks.register("runActive$type") {
        group = "project"
        dependsOn(":${stonecutter.current!!.project}:run$type")
    }
}
