package tim

import java.io.File
import java.nio.file.Paths
import java.util.*

fun main() {
    val path = Paths.get(File("/Users/swathimakkena/workspace/stufftodo/application/runner/build/libs/todoer").toURI())

    val file = path.toFile()

    val fileStack: Stack<File> = Stack()
    fileStack.push(file)

    traverseTheFiles(fileStack)
}

fun traverseTheFiles(fileStack: Stack<File>) {
    val visited: HashSet<String> = HashSet()

    while (!fileStack.empty()) {
        val file = fileStack.pop()
        visited.add(file.absolutePath)

        if (file.isDirectory) {
            addNewFiles(fileStack, visited, file.listFiles())
        } else {
            val absolutePath = file.absolutePath
            if (absolutePath.endsWith(".RSA") || absolutePath.endsWith(".SF") || absolutePath.endsWith(".DSA")) {
                println(absolutePath)
            }
        }
    }
}

private fun addNewFiles(
    fileStack: Stack<File>,
    visited: HashSet<String>,
    arrayOfFiles: Array<out File>
) {
    arrayOfFiles
        .filter {
            !visited.contains(it.absolutePath)
        }
        .forEach {
            fileStack.push(it)
        }
}
