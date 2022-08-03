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
                val path = rootProject.layout.projectDirectory.dir("dist")
                args(
                    path, // input
                    path.asFile.absolutePath // output
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
