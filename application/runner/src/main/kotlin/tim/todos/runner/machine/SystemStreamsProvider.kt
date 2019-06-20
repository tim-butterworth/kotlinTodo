package tim.todos.runner.machine

import tim.todos.runner.machine.StreamsProvider
import java.io.InputStream
import java.io.PrintStream

class SystemStreamsProvider : StreamsProvider {
    override fun getInputStream(): InputStream = System.`in`
    override fun getNormalOutputStream(): PrintStream = System.out
    override fun getErrorOutputStream(): PrintStream = System.err
}