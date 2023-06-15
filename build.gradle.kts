plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"

}
group = "com.unongmilk"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

project.extra.set("packageName", name.replace("-", ""))
project.extra.set("pluginName", name.split('-').joinToString("") { it.capitalize() })

repositories {
    mavenLocal()
    mavenCentral()
    maven (url= "https://repo.dmulloy2.net/repository/public/" )
}

dependencies {
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
}

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
            expand(project.extra.properties)
        }
    }

    test {
        useJUnitPlatform()
    }

    create<Jar>("paperJar") {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("") // For bukkit plugin update

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}
