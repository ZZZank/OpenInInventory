repositories {
    /**
     * Restricts dependency search of the given [groups] to the [url], improving setup speed.
     */
    fun strictMaven(name: String, url: String, vararg groups: String) = exclusiveContent {
        forRepository {
            maven(url) {
                this.name = name
            }
        }
        filter {
            groups.forEach(this::includeGroup)
        }
    }

    mavenCentral()
    strictMaven("CurseMaven", "https://cursemaven.com", "curse.maven")
    strictMaven("Modrinth", "https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("BlameJared", "https://maven.blamejared.com", "com.blamejared.crafttweaker", "vazkii.patchouli")
    strictMaven("Latvian", "https://maven.latvian.dev/releases", "dev.latvian.mods", "dev.latvian.apps")
    maven("https://maven.architectury.dev/") { name = "Architectury" }
}
