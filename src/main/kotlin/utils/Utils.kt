package utils

import utils.actions.setFailed

/**
 * Will automatically either report the message to the log, or mark the action as failed. Additionally defining the
 * output failed, allowing it to be read in by other actions
 */
fun failOrError(message: String) = setFailed(message)
