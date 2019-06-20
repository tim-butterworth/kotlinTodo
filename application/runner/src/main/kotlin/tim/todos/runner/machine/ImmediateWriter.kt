package tim.todos.runner.machine

import java.io.OutputStreamWriter

class ImmediateWriter(private val streamWriter: OutputStreamWriter) : AutoCloseable {
    override fun close() {
        streamWriter.flush()
        streamWriter.close()
    }

    fun write(message: String) {
        streamWriter.write("$message\n")
        streamWriter.flush()
    }
}
