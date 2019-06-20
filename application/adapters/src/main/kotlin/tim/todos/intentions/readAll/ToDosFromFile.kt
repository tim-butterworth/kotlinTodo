package tim.todos.intentions.readAll

import arrow.core.Either
import tim.todo.intentions.ReadFailure
import tim.todo.intentions.ToDo
import tim.todo.intentions.readAll.AllToDoProvider
import tim.todos.ToDosReader

class ToDosFromFile(
    private val reader: ToDosReader
) : AllToDoProvider {
    override fun getAll(): Either<ReadFailure, List<ToDo>> = reader.allTodos().map {
        it.todos
    }
}