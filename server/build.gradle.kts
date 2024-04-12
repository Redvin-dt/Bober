plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.protobuf") version "0.9.4"
    application
    java
}

group = "ru.hse"
version = "0.0.1-SNAPSHOT"


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // apache
    implementation("commons-io:commons-io:2.15.1")
    implementation(group = "org.apache.logging.log4j", name = "log4j-api", version = "2.20.0")
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.20.0")
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.20.0")
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    //test
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //db
    implementation("org.postgresql:postgresql:42.7.2")
    //protobuf
    implementation("com.google.protobuf:protobuf-java:3.25.3")
    implementation("com.google.protobuf:protobuf-java-util:3.25.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    generateProtoTasks {
        ofSourceSet("main")
    }
}



application {
    mainClass = "ru/hse/server/ServerApplication"
}

sourceSets {
    main {
        proto {
            srcDir("../proto")
            srcDir("src/main/protocolbuffers")
            include("**/*.protodevel")
        }
        java {

        }
    }
    test {
        proto {
            srcDir("src/test/protocolbuffers")
        }
    }
}



