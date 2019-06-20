package tim.todos.intentions.add

import tim.todo.intentions.add.ToDoWriter
import tim.todos.configuration.getConfiguredObjectMapper
import tim.todos.io.AbsoluteFileAccessor

class AddToDoWriterFactory {
    fun withAbsoluteFileAccessor(absoluteFileAccessor: String): ToDoWriter {
        val configuredObjectMapper = getConfiguredObjectMapper()

        return ToDoToFile(configuredObjectMapper, AbsoluteFileAccessor(absoluteFileAccessor))
    }
}