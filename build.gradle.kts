plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
}
version = property("mod_version")!!
group = property("maven_group")!!
val minecraftVersion = property("minecraft_version")!!
val baseName = property("archives_base_name").toString()

repositories {
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
}

tasks {
    processResources {
        files("fabric.mod.json") {
            expand("version" to project.version)
            expand("minecraft_version" to minecraftVersion)
        }
    }
    
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
    }
    
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
java{
//    withJavadocJar()
    withSourcesJar()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = baseName
            version = version
            println(getComponents().toList())
            from(getComponents()["java"])
        }
        
    }
    repositories{
        repositories.mavenLocal()
    }
}