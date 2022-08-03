package utils.actions

import NodeJS.get
import kotlin.reflect.KProperty
import process

/**
 * See [https://docs.github.com/en/actions/configuring-and-managing-workflows/using-environment-variables#default-environment-variables]
 */
object ActionsEnvironment {
    val GITHUB_TOKEN by Environment
}

private object Environment {
    operator fun getValue(environment: Any, property: KProperty<*>): String =
        process.env[property.name] ?: throw ActionFailedException("${property.name} is not found in process.env")
}
