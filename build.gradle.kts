plugins {
    kotlin("js") version "1.7.10"
    // TODO: Is this needed?
    kotlin("plugin.serialization") version "1.7.10"
}

group = "dev.wotton"
version = "1.0"

repositories {
    mavenCentral()
    // TODO: Find replacement
    jcenter()
}

dependencies {
    // TODO: Go through whether all of these are needed
    compileOnly(devNpm("google-closure-compiler", "20210808.0.0")) // v20220719
    implementation(npm("@actions/core", "1.4.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("io.ktor:ktor-client-js:1.5.4")
    implementation("io.ktor:ktor-client-serialization:1.5.4")
    implementation("io.ktor:ktor-client-logging-js:1.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
}

kotlin {
    js(IR) {
        binaries.executable()
        useCommonJs()
        nodejs {}
    }
}

val CopyGeneratedJSToDistribution = tasks.register<Copy>("CopyGeneratedJSToDistribution") {
    finalizedBy(project(":ncc-packer").tasks.named("run"))
    from("${buildDir}/compileSync/main/productionExecutable/kotlin/milestone-changelog-creator.js") {
        rename("milestone-changelog-creator", "index")
    }
    into("$rootDir/dist")
}

val optimizeJs = tasks.register<Exec>("optimizeJs") {
    dependsOn(CopyGeneratedJSToDistribution)
    val inputFileName = "${rootDir}/dist/index.js"
    val outputFileName = "${rootDir}/dist/index-optimized.js"
    inputs.file(inputFileName)
    outputs.file(outputFileName)
    outputs.upToDateWhen { true }
    outputs.cacheIf { true }
    commandLine(
        "node",
        "${File(rootProject.buildDir, "js/node_modules/google-closure-compiler/cli.js")}",
        "--js=${inputFileName}",
        "--js_output_file=${outputFileName}",
        "-O=SIMPLE",
        "--env=BROWSER",
        "--warning_level=QUIET",
    )
}

tasks.named("assemble") {
    finalizedBy(optimizeJs)
}
