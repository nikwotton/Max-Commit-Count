import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js")
}

kotlin {
    js(LEGACY) {
        useCommonJs()
        binaries.executable()
        nodejs {
            runTask {
                val inputFile =
                    "${rootProject.buildDir}/compileSync/main/productionExecutable/kotlin/Max-Commit-Count.js"
                val outputDir = rootProject.layout.projectDirectory.dir("dist")
                inputs.file(inputFile)
                outputs.dir(outputDir)
                outputs.upToDateWhen { true }
                outputs.cacheIf { true }
                args(
                    inputFile,
                    outputDir
                )
            }
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.363")
    implementation(npm("@vercel/ncc", "0.34.0", generateExternals = false))
}
// test
val TaskContainer.compileKotlinJs
    get() = named<Kotlin2JsCompile>("compileKotlinJs")
