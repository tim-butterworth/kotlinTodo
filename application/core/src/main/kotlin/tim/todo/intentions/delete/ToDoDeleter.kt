package tim.todo.intentions.delete

import arrow.core.Either
import java.util.*

interface ToDoDeleter {
    fun deleteToDo(uuid: UUID): Either<DeleteFailure, DeleteSuccess>
}
