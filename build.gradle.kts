buildscript {

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("commons-io:commons-io:2.11.0")

    }
}


plugins {
    id("java")
    id("idea")
}

group = "me.roadroller01"
version = "1.0-SNAPSHOT"

//idea {
//    module {
//        downloadJavadoc = false
//        downloadSources = true
//    }
//}



repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.1.81.Final")

    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")
}