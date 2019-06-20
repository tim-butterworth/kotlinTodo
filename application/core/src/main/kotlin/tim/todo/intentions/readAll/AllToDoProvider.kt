package tim.todo.intentions.readAll

import arrow.core.Either
import tim.todo.intentions.ReadFailure
import tim.todo.intentions.ToDo

interface AllToDoProvider {
    fun getAll(): Either<ReadFailure, List<ToDo>>
}
