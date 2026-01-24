plugins {
    `java-library`
    id("com.bakdata.mockito") version "1.11.1"
}

group = "me.supcheg"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
