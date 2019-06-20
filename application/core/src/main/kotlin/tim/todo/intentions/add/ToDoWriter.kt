package tim.todo.intentions.add

import arrow.core.Either
import tim.todo.intentions.ToDoData
import tim.todo.intentions.WriteFailure
import java.util.*

interface ToDoWriter {
    fun write(todoData: ToDoData): Either<WriteFailure, UUID>
}
