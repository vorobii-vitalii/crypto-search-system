plugins {
    id("java")
}

val mainClass = "org.vitalii.vorobii.CryptoIndexerBatch"

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to mainClass)
        }
        from(configurations.runtimeClasspath.get()
//            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
//        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}

group = "org.example"

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