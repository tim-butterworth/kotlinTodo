package tim.todos

import com.fasterxml.jackson.databind.ObjectMapper
import tim.todos.intentions.ToDosContainer
import tim.todos.io.AbsoluteFileAccessor
import java.io.File

class ToDosWriter(
    private val objectMapper: ObjectMapper,
    private val absoluteFileAccessor: AbsoluteFileAccessor
) {
    fun replaceToDos(toDosContainer: ToDosContainer) {
        val todosFile = File(absoluteFileAccessor.getAbsolutePath())

        objectMapper.writeValue(todosFile, toDosContainer)
    }
}