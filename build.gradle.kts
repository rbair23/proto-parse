
plugins {
    java
    idea
    `maven-publish`
    id("me.champeau.jmh") version "0.6.6"
    id("com.google.protobuf") version "0.8.19"
}

group = "com.hedera.hashgraph.protoparse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("com.google.protobuf:protobuf-java:3.21.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

jmh {
    jmhVersion.set("1.35")
}

tasks.jmhJar {
    manifest {
        attributes(mapOf("Multi-Release" to true))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.hedera.hashgraph.protoparse"
            artifactId = "proto-parse"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}
