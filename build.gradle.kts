plugins {
    java
}

group = "dev.reeve"
version  = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.bukkit:craftbukkit:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    implementation("com.google.code.gson:gson:2.8.6")
}