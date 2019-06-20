package tim.todos.intentions.add

import tim.todo.intentions.WriteFailure
import tim.todo.intentions.add.AddToDoResponder
import tim.todos.intentions.MessageDispatcher
import java.util.*

class AddToDoResponderCommandLine(
    val successDispatcher: MessageDispatcher<String>,
    val errorDispatcher: MessageDispatcher<String>
) : AddToDoResponder<Unit> {
    override fun success(id: UUID) {
        successDispatcher(id.toString())
    }

    override fun error(failure: WriteFailure) {
        errorDispatcher("Failed to write")
    }
}