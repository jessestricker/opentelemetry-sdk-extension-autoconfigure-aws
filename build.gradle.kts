plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.opentelemetry.instrumentation.bom))
    implementation(platform(libs.awssdk.bom))

    compileOnly(libs.opentelemetry.sdk.extension.autoconfigure.spi)
    implementation(libs.awssdk.secretsmanager) {
        exclude(libs.awssdk.apacheClient)
        exclude(libs.awssdk.nettyNioClient)
    }
    runtimeOnly(libs.awssdk.urlConnectionClient)
    implementation(libs.gson)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.opentelemetry.sdk.extension.autoconfigure.spi)
}

tasks.withType<JavaCompile> {
    options.release = libs.versions.java.map { it.toInt() }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

spotless {
    java {
        googleJavaFormat()
    }
}

fun <T : ModuleDependency> T.exclude(provider: Provider<out Dependency>): T {
    val dependency = provider.get()
    return exclude(group = dependency.group, module = dependency.name)
}
