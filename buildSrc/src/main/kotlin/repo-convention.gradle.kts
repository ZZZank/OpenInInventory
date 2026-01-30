
repositories {
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
    strictMaven("Patchouli-only", "https://maven.blamejared.com", "vazkii.patchouli")
    strictMaven("Explosion Gradle Plugin", "https://maven2.bai.lol", "lol.bai.explosion")
    maven { url = uri("https://maven.architectury.dev/") }
}