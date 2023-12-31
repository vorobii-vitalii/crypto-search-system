import com.google.protobuf.gradle.*

plugins {
    id("java")
    id("com.google.protobuf") version "0.8.19"
}

val mainClass = "org.vitalii.vorobii.CryptoSearchServer"

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to mainClass)
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
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

    // Elastic search
    implementation("co.elastic.clients:elasticsearch-java:8.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

    // Logging
    implementation("org.slf4j:slf4j-api:2.1.0-alpha0")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha0")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.50")
    annotationProcessor("com.google.dagger:dagger-compiler:2.50")

    implementation("com.google.protobuf:protobuf-java:3.22.2")
    implementation("io.grpc:grpc-stub:1.53.0")
    implementation("io.grpc:grpc-protobuf:1.53.0")
    implementation("io.grpc:grpc-netty:1.60.1")

    implementation("javax.annotation:javax.annotation-api:1.3.1")

    // Reactor
    implementation("io.projectreactor:reactor-core:3.6.1")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without
                // options. Note the braces cannot be omitted, otherwise the
                // plugin will not be added. This is because of the implicit way
                // NamedDomainObjectContainer binds the methods.
                id("grpc") { }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}