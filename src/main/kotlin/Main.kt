import NodeJS.get
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import models.Inputs
import utils.actions.getInput
import utils.actions.setFailed

suspend fun main() {
    val inputs = resolveInputs()
    val numCommits: Int
    try {
        val client = HttpClient(Js) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json)
            }
        }
        numCommits = client.get<JsonObject>(inputs.repoUrl) {
            header("Accept", "application/vnd.github+json")
            header("Authorization", "token ${inputs.token}")
        }["commits"]!!.jsonPrimitive.toString().toInt()
        client.close()
    } catch (e: Exception) {
        setFailed("Max-Commit-Count failed to run due to exception")
        throw e
    }
    if (numCommits > inputs.maxCommits) {
        setFailed("Your PR contains $numCommits commits, but you are only allowed up to ${inputs.maxCommits} per PR")
    }
    println("You are allowed ${inputs.maxCommits} commits and you only have $numCommits. Passed!")
}

fun resolveInputs(): Inputs {
    val inputString =
        getInput("maxCommits").ifEmpty { throw IllegalArgumentException("Don't forget to define maxCommits") }
    val inputNumber = inputString.toIntOrNull() ?: throw IllegalArgumentException("$inputString is not a valid number")
    val token =
        getInput("token").ifEmpty { getInput("GITHUB_TOKEN").ifEmpty { throw IllegalArgumentException("Don't forget to set token") } }
    val repo = getEnv("GITHUB_REPOSITORY")
    val prNumber = getEnv("GITHUB_REF_NAME").split("/")[0].toIntOrNull()
        ?: throw IllegalStateException("Failed to parse PR number")
    require(getEnv("GITHUB_REF_TYPE") == "branch") { "This step should only be triggered by pull requests" }
    require(getEnv("GITHUB_EVENT_NAME") == "pull_request") { "This step should only be triggerd by a pull request" }
    val repoUrl = "https://api.github.com/repos/$repo/pulls/$prNumber"
    return Inputs(maxCommits = inputNumber, token = token, repoUrl = repoUrl)
}

fun getEnv(name: String): String {
    val errorString = "Somehow unable to find $name"
    return process.env[name]?.ifEmpty { throw IllegalStateException(errorString) } ?: throw IllegalStateException(
        errorString
    )
}
