import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("java")
    id("com.google.protobuf") version "0.8.17"
}

group = "zm.gov.moh.hie"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.grpc:grpc-netty:1.49.0")
    implementation("io.grpc:grpc-protobuf:1.49.0")
    implementation("io.grpc:grpc-stub:1.49.0")
    implementation("com.google.protobuf:protobuf-java:3.21.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.5"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.49.0"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                java {}
            }
            it.plugins {
                id("grpc")
            }
        }
    }
}
