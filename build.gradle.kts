plugins {
    kotlin("js") version "1.7.10"
}

group = "dev.wotton"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(npm("@octokit/core", "4.0.4", generateExternals = true))
    testImplementation(kotlin("test"))
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs {

        }
    }
}

tasks.register<Copy>("CopyGeneratedNodeModuleToRoot") {
    from("${buildDir}/js/node_modules") {
        exclude("**/.bin")
    }
    into("$rootDir/node_modules")
}

tasks.register<Copy>("CopyGeneratedJSToDistribution") {
    dependsOn("CopyGeneratedNodeModuleToRoot")
    from("${buildDir}/compileSync/main/productionExecutable/kotlin/milestone-changelog-creator.js") {
        rename("milestone-changelog-creator", "index")
    }
    into("$rootDir/dist")
}

tasks.named("assemble") {
    finalizedBy("CopyGeneratedNodeModuleToRoot")
}

tasks.named("CopyGeneratedNodeModuleToRoot") {
    finalizedBy("CopyGeneratedJSToDistribution")
}
