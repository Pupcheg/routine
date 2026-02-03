import com.diffplug.spotless.FormatterFunc
import java.io.Serializable

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

        custom("add missing since tag", object : FormatterFunc, Serializable {
            val markdownJavadoc = Regex("(?m)^\\s*\\/\\/\\/.*(?:\\n\\s*\\/\\/\\/.*)*")

            override fun apply(source: String): String {
                val missing = markdownJavadoc.findAll(source)
                    .filter { !it.value.contains("@since") }
                    .map { "@since tag required at line ${source.substring(0, it.range.last).lines().size}" }
                    .toList()
                if (missing.isNotEmpty()) {
                    throw AssertionError(missing.joinToString("\n"))
                }
                return source
            }
        })
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

tasks {
    test {
        useJUnitPlatform()
    }
}
