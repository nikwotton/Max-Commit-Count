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
    val inputString =
        getInput("maxCommits").ifEmpty { throw IllegalArgumentException("Don't forget to define maxCommits") }
    val inputNumber = inputString.toIntOrNull() ?: throw IllegalArgumentException("$inputString is not a valid number")
    val token = getInput("token").ifEmpty { ActionsEnvironment.GITHUB_TOKEN }
    return@group Inputs(
        maxCommits = inputNumber,
        token = token
    )
}
