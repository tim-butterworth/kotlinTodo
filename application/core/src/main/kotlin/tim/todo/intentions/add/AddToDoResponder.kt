package tim.todo.intentions.add

import tim.todo.intentions.WriteFailure
import java.util.*

interface AddToDoResponder<T> {
    fun success(id: UUID): T
    fun error(failure: WriteFailure): T
}
