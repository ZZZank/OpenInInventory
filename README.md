# Open In Inventory

Open guidebooks, backpacks, handhelds and more with a single click in inventory.

Multi-version Minecraft mod built with [Stonecutter](https://stonecutter.kikugie.dev), supporting Fabric, (legacy) Forge
and NeoForge from a single codebase.

Use `./gradlew buildAndCollect` to build the mod for every registered node. Resulting jars are copied to
`./build/libs/{mod version}/`, with the loader and Minecraft version in the filename, e.g.
`open_in_inventory-fabric-2.0.2+1.20.1.jar`.

Commonly used mavens are registered in [repo-convention.gradle.kts](./buildSrc/src/main/kotlin/repo-convention.gradle.kts).

## Project Structure

- `src`: shared source code, linked into every node by Stonecutter
- `fabric/src`, `forge/src`, `neoforge/src`: loader-specific sources and metadata
- `build.fabric.gradle.kts`, `build.forge.gradle.kts`, `build.neoforge.gradle.kts`: per-loader build scripts
- `stonecutter.properties.toml`: all mod/dependency/publishing properties, per version and per loader
- `stonecutter.gradle.kts`: active node, Stonecutter parameters
- `buildSrc/src/main/kotlin`: shared repository setup and the Minecraft artifact build-service mutex

Stonecutter generates one project per node, named `versions/{minecraft}-{loader}`.

### Example: 1.20.1-forge

- `:versions:1.20.1-forge` — the Gradle project for this node
- `build.forge.gradle.kts` — its build script
- `[forge."1.20.1"]` in `stonecutter.properties.toml` — its properties
- `forge/src` — Forge specific sources and resources
- `src` — shared sources

## Development

```bash
./gradlew runActiveClient        # client of the active node
./gradlew runActiveServer        # server of the active node
./gradlew :versions:1.20.1-forge:runClient
```

The active node is stored in [stonecutter.gradle.kts](./stonecutter.gradle.kts) and can be changed with the Stonecutter
IntelliJ plugin or by editing that line.

## Publishing

This project uses [ModPublisher](https://github.com/firstdarkdev/modpublisher): `publishMod`, `publishCurseforge`,
`publishGitHub` and `publishModrinth` tasks are available under the `publishing` section of the Gradle tasks.
`publishMod` publishes to every valid platform.

| Platform   | Properties ([stonecutter.properties.toml](./stonecutter.properties.toml)) | GitHub secret    |
|------------|--------------------------------------------------------------------------|------------------|
| GitHub     | `publish.github`                                                         | `GITHUB_TOKEN`   |
| Modrinth   | `publish.modrinth`                                                       | `MODRINTH_TOKEN` |
| CurseForge | `publish.curseforge`                                                     | `CURSE_TOKEN`    |

`publish.type` selects the release channel (`release`, `beta`, `alpha`), or `debug` to skip uploading. It can be
overridden from the command line:

```bash
./gradlew publishMod -Ppublish.type=debug --no-configuration-cache
```

Note: in GitHub Actions, `GITHUB_TOKEN` is generated automatically, so GitHub publishing happens whenever
`publish.github` is set.

## Related Resources

- Stonecutter: [IDEA plugin](https://plugins.jetbrains.com/plugin/25044-stonecutter-dev), [WIKI](https://stonecutter.kikugie.dev/wiki/), [Homepage](https://stonecutter.kikugie.dev/)
- Loom backwards compatibility: [loom-back-compat](https://codeberg.org/KikuGie/loom-back-compat)
- ModDevGradle: [docs](https://docs.neoforged.net/toolchain/docs/plugins/mdg/), [legacy Forge](https://github.com/neoforged/ModDevGradle/blob/main/LEGACY.md)
- Architectury API: [WIKI](https://docs.architectury.dev/)
- Fabric: [WIKI](https://docs.fabricmc.net/)
- Forge: [WIKI](https://docs.minecraftforge.net/)
