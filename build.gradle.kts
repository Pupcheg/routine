plugins {
    `java-library`
    id("com.bakdata.mockito") version "1.11.1"
    id("com.diffplug.spotless") version "8.2.0"
}

group = "me.supcheg"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

spotless {
    java {
        palantirJavaFormat()

        importOrder("", "java|javax", "\\#")
        removeUnusedImports()
        forbidWildcardImports()

        targetExclude("build/**")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
