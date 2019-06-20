package tim.todos.intentions.delete

import arrow.core.Either
import arrow.core.flatMap
import tim.todo.intentions.ToDo
import tim.todo.intentions.delete.*
import tim.todos.ToDosReader
import tim.todos.ToDosWriter
import tim.todos.intentions.ToDosContainer
import tim.todos.operators.find
import java.io.IOException
import java.util.*

class DeleteToDoFromFile(
    private val reader: ToDosReader,
    private val writer: ToDosWriter
) : ToDoDeleter {
    override fun deleteToDo(uuid: UUID): Either<DeleteFailure, DeleteSuccess> {
        val ifEmpty = { Either.left(ToDoNotFound) }
        val ifSome = { v: Either<DeleteFailure, DeleteSuccess> -> v }

        val toDosContainerOrError = reader.allTodos()

        return toDosContainerOrError.mapLeft { IOError }.flatMap { toDosContainer ->
            find(toDosContainer.todos) { todo ->
                todo.id == uuid
            }.map { _ ->
                tryWritingToDos(writer, toDosContainer.todos.filter { it.id != uuid })
            }.fold(ifEmpty, ifSome)
        }
    }

    private fun tryWritingToDos(
        writer: ToDosWriter,
        filtered: List<ToDo>
    ): Either<DeleteFailure, DeleteSuccess> {
        return try {
            writer.replaceToDos(ToDosContainer(todos = filtered))

            Either.right(DeleteSuccess())
        } catch (ioException: IOException) {
            Either.left(IOError)
        }
    }
}