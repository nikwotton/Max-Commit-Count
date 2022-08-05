import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js")
}

val tmpDir = "${rootProject.buildDir}/dist-temp"

val copyDir = tasks.register("copyDir") {
    val inputDir = "${rootProject.buildDir}/compileSync/main/productionExecutable/kotlin/"
    val outputDir = tmpDir
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    outputs.upToDateWhen { true }
    outputs.cacheIf { true }
    doLast {
        val output = File(outputDir)
        if (output.exists())
            output.deleteRecursively()
        File(inputDir).copyRecursively(output, overwrite = true)
        val originIndex = File("$outputDir/Max-Commit-Count.js")
        originIndex.copyTo(File("$outputDir/index.js"))
        originIndex.delete()
    }
}

kotlin {
    js(LEGACY) {
        useCommonJs()
        binaries.executable()
        nodejs {
            runTask {
                dependsOn(copyDir)
                val inputFile = tmpDir
                val outputDir = rootProject.layout.projectDirectory.dir("dist")
                inputs.dir(inputFile)
                outputs.dir(outputDir)
                outputs.upToDateWhen { true }
                outputs.cacheIf { true }
                args(
                    "$inputFile/index.js",
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

val TaskContainer.compileKotlinJs
    get() = named<Kotlin2JsCompile>("compileKotlinJs")
