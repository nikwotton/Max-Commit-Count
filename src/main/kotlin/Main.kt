import models.Inputs
import utils.actions.ActionsEnvironment
import utils.actions.getInput
import utils.actions.group
import utils.failOrError

fun main() {
    val inputs = resolveInputs()
    print("$inputs")
    try {
        println("Hello World From GitHub Actions!")
    } catch (e: Exception) {
        failOrError(e.message ?: "Error while generating changelog")
    }
}

fun resolveInputs() = group("Reading input values") {
    return@group Inputs(
        maxCommits = getInput("maxCommits").toInt(),
        token = getInput("token").ifEmpty { ActionsEnvironment.GITHUB_TOKEN }
    )
}
