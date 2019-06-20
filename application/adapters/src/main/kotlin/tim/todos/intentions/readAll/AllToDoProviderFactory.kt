package tim.todos.intentions.readAll

import tim.todo.intentions.readAll.AllToDoProvider
import tim.todos.ToDosReader
import tim.todos.configuration.getConfiguredObjectMapper
import tim.todos.io.AbsoluteFileAccessor

class AllToDoProviderFactory {
    fun withAbsoluteFileAccessor(absoluteFilePath: String): AllToDoProvider {
        val reader = ToDosReader(
            getConfiguredObjectMapper(),
            AbsoluteFileAccessor(absoluteFilePath)
        )

        return ToDosFromFile(reader)
    }
}