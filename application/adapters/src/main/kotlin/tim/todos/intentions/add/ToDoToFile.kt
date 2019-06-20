package tim.todos.intentions.add

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import tim.todo.intentions.ToDo
import tim.todo.intentions.ToDoData
import tim.todo.intentions.WriteFailure
import tim.todo.intentions.add.ToDoWriter
import tim.todos.intentions.ToDosContainer
import tim.todos.io.FileAccessor
import java.io.File
import java.util.*

class ToDoToFile(private val objectMapper: ObjectMapper, private val fileAccessor: FileAccessor) : ToDoWriter {
    override fun write(todoData: ToDoData): Either<WriteFailure, UUID> {
        val nextId = UUID.randomUUID()

        val toDosContainerFromFile = objectMapper.readValue(File(fileAccessor.getAbsolutePath()), ToDosContainer::class.java)
        val toDosContainer = ToDosContainer(
            todos = listOf(*toDosContainerFromFile.todos.toTypedArray(), ToDo(nextId, todoData.title, todoData.description))
        )

        objectMapper.writeValue(File(fileAccessor.getAbsolutePath()), toDosContainer)

        return Either.right(nextId)
    }
}