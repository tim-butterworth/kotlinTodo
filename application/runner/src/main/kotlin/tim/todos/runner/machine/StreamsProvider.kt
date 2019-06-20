package tim.todos.runner.machine

import java.io.InputStream
import java.io.PrintStream

interface StreamsProvider {
    fun getInputStream(): InputStream
    fun getNormalOutputStream(): PrintStream
    fun getErrorOutputStream(): PrintStream
}
