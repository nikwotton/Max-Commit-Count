import models.Inputs
import utils.actions.ActionsEnvironment
import utils.actions.getInput
import utils.actions.group
import utils.failOrError

fun main() {
    println("HIT THE FUCKING ENTRY POINT")
    val inputs = resolveInputs()
    print("$inputs")
    try {
        println("Hello World From GitHub Actions!")
    } catch (e: Exception) {
        failOrError(e.message ?: "Error while generating changelog")
    }
}

fun resolveInputs() = group("Reading input values") {
    println("Resolving Inputs")
    val inputString = getInput("maxCommits")
    println("Got input string: $inputString")
    println("Number version: ${inputString.toInt()}")
    val maxCommits = (getInput("maxCommits") ?: "").toInt()
    val token = getInput("token").ifEmpty { ActionsEnvironment.GITHUB_TOKEN }
    return@group Inputs(
        maxCommits = maxCommits,
        token = token
    )
}
