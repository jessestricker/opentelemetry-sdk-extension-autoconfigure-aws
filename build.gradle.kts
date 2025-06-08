plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:1.51.0")

    implementation(platform("software.amazon.awssdk:bom:2.31.59"))
    implementation("software.amazon.awssdk:secretsmanager") {
        exclude("software.amazon.awssdk", "apache-client")
        exclude("software.amazon.awssdk", "netty-nio-client")
    }
    runtimeOnly("software.amazon.awssdk:url-connection-client")

    implementation("com.google.code.gson:gson:2.13.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.release = 11
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
