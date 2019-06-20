package tim.todo.intentions.delete

import java.util.*

interface DeleteToDoResponder<T> {
    fun success(): T
    fun error(failedToDeleteId: UUID): T
}
