package nccpacker

import fs.MakeDirectoryOptions
import fs.`T$45`
import fs.mkdirSync
import fs.writeFileSync
import kotlinx.coroutines.await
import kotlinx.js.jso
import path.path
import process

suspend fun main() {
    runCatching {
        val (input, output) = process.argv.filterIndexed { i, _ -> i > 1 }
        val result = ncc(input, jso {
            sourceMap = true
            license = "LICENSES"
        }).await()

        mkdirSync(output, jso<MakeDirectoryOptions> {
            recursive = true
        })
        writeFileSync(path.join(output, "index.js"), result.code, jso<`T$45`>())
        result.map?.also { writeFileSync(path.join(output, "index.js.map"), it, jso<`T$45`>()) }

        result.assets?.forEach { (assetFileName, asset) ->
            val assetFilePath = path.join(output, assetFileName)
            mkdirSync(path.dirname(assetFilePath), jso<MakeDirectoryOptions> {
                recursive = true
            })
            writeFileSync(assetFilePath, asset.source, jso<`T$45`> {
                mode = asset.permissions
            })
        }
    }.onFailure {
        console.error(it)
        process.exit(1)
    }
}
