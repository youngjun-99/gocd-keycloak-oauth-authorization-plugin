/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

group = "cd.go"
version = "3.0.1"

val pluginId = "cd.go.authorization.keycloak"
val pluginVersion = project.version.toString()
val goCdVersion = "25.4.0"
val pluginName = "Keycloak oauth authorization plugin"
val pluginDescription = "Keycloak oauth authorization plugin for GoCD"
val vendorName = "youngjun-99"
val vendorUrl = "https://github.com/youngjun-99/gocd-keycloak-oauth-authorization-plugin"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.toVersion(21)
    targetCompatibility = JavaVersion.toVersion(21)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly("cd.go.plugin:go-plugin-api:23.3.0")
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("cd.go.plugin:go-plugin-api:25.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("org.jsoup:jsoup:1.17.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    doLast {
        listOf("MD5", "SHA1", "SHA-256").forEach { algo ->
            ant.withGroovyBuilder {
                "checksum"("file" to archiveFile.get(), "format" to "MD5SUM", "algorithm" to algo)
            }
        }
    }

    manifest {
        attributes(
            "Go-Version" to goCdVersion,
            "Plugin-Revision" to pluginVersion,
            "Implementation-Title" to pluginName,
            "Implementation-Version" to pluginVersion,
            "Source-Compatibility" to java.sourceCompatibility,
            "Target-Compatibility" to java.targetCompatibility
        )
    }
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "src/main/resources-generated")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.jar {
    archiveBaseName.set("keycloak-oauth-authorization-plugin")
    from(configurations.runtimeClasspath.get()) {
        into("lib/")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

githubRelease {
    val repoSlug = (System.getenv("GITHUB_REPOSITORY") ?: "youngjun-99/gocd-keycloak-oauth-authorization-plugin").split("/")
    owner(repoSlug[0])
    repo(if (repoSlug.size > 1) repoSlug[1] else "gocd-keycloak-oauth-authorization-plugin")
    token(System.getenv("GITHUB_TOKEN") ?: "")
    tagName("v${project.version}")
    releaseName("v${project.version}")
    targetCommitish("main")
    prerelease(!"No".equals(System.getenv("PRERELEASE"), ignoreCase = true))
    releaseAssets(tasks.jar.get().outputs.files)
}
