package tim.todo.intentions.delete

import java.util.*

class DeleteToDo<T>(private val deleter: ToDoDeleter, private val responder: DeleteToDoResponder<T>) {
    fun execute(uuid: UUID): T {
        val deletionResult = deleter.deleteToDo(uuid)

        return deletionResult.fold(
            { responder.error(uuid) },
            { responder.success() }
        )
    }
}