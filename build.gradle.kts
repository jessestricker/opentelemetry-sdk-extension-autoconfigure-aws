plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:1.51.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}
