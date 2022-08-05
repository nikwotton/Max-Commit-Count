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
    compileOnly(devNpm("google-closure-compiler", "20220719.0.0"))
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

val assemble = tasks.named("assemble")

project(":ncc-packer").afterEvaluate {
    this.tasks.named("run").configure { dependsOn(assemble) }
}

val optimizeJs = tasks.register<Exec>("optimizeJs") {
    dependsOn(project(":ncc-packer").tasks.named("run"))
    val indexFileName = "$rootDir/dist/index.js"
    val compilerDir = "$buildDir/js/node_modules/google-closure-compiler"
    val compilerCli = "$compilerDir/cli.js"
    val outputFileName = "$rootDir/dist/index-optimized.js"
    inputs.file(indexFileName)
    inputs.dir(compilerDir)
    outputs.file(outputFileName)
    outputs.upToDateWhen { true }
    outputs.cacheIf { true }
    commandLine(
        "node",
        compilerCli,
        "--js=${indexFileName}",
        "--js_output_file=${outputFileName}",
        "-O=WHITESPACE_ONLY",
    )
}

tasks.named("assemble") {
    finalizedBy(optimizeJs)
}
