package testoffset.tim.todos.intentions.testFileHelpers

import java.io.FileWriter
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

class TestFiles

fun TestFiles.withTemporaryFile(fileContent: String, body: (filePath: String) -> Unit) {
    val fileToCreate = this.javaClass.classLoader.getResource("").toURI().toString() + "/valid_populated_file.json"
    val path = Paths.get(URI.create(fileToCreate))

    try {
        Files.createFile(path)

        val file = path.toFile()

        FileWriter(file).use {
            it.write(fileContent)
        }

        body(path.toString())

    } finally {
        Files.delete(path)
    }
}