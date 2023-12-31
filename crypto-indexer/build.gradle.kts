plugins {
    id("java")
    `maven-publish`
}

val mainClass = "org.vitalii.vorobii.CryptoIndexerBatch"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/vorobii-vitalii/crypto-search-system")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name = "Crypto indexer"
                description = "Crypto indexer to ElasticSearch"
                url = "https://github.com/vorobii-vitalii/crypto-search-system"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "vorobii-vitalii"
                        name = "Vitalii Vorobii"
                        email = "vitalij.vorobij@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:ssh://github.com/vorobii-vitalii/crypto-search-system.git"
                    developerConnection = "scm:git:ssh://github.com/vorobii-vitalii/crypto-search-system.git"
                    url = "https://github.com/vorobii-vitalii/crypto-search-system"
                }
            }
        }
    }
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to mainClass)
        }
        from(configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        from(sourcesMain.output)
    }
}

group = "org.vitalii.vorobii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Tests
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("io.projectreactor:reactor-test:3.6.1")

    // Elastic search
    implementation("co.elastic.clients:elasticsearch-java:8.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

    // Tracing
    implementation("co.elastic.apm:apm-agent-api:1.45.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.1.0-alpha0")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha0")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.50")
    annotationProcessor("com.google.dagger:dagger-compiler:2.50")

    // Reactor
    implementation("io.projectreactor:reactor-core:3.6.1")
}

tasks.test {
    useJUnitPlatform()
}