package tim.todos.intentions.delete

import tim.todo.intentions.delete.ToDoDeleter
import tim.todos.ToDosReader
import tim.todos.ToDosWriter
import tim.todos.configuration.getConfiguredObjectMapper
import tim.todos.io.AbsoluteFileAccessor

class ToDoDeleterFactory {
    fun getInstance(absoluteFilePath: String): ToDoDeleter {
        val objectMapper = getConfiguredObjectMapper()
        return DeleteToDoFromFile(
            ToDosReader(
                objectMapper,
                AbsoluteFileAccessor(absoluteFilePath)
            ),
            ToDosWriter(
                objectMapper,
                AbsoluteFileAccessor(absoluteFilePath)
            )
        )
    }
}