package tim.todos.intentions.delete

import tim.todo.intentions.delete.DeleteToDoResponder
import tim.todos.intentions.BlockingMessageDispatcher
import java.util.*

class DeleteToDoResponderCommandLine(
    private val successDispatcher: BlockingMessageDispatcher<String>,
    private val errorDispatcher: BlockingMessageDispatcher<String>
) : DeleteToDoResponder<Unit> {
    override fun success() {
        successDispatcher("Successfully deleted!")
    }

    override fun error(failedToDeleteId: UUID) {
        errorDispatcher("Failed to delete todo $failedToDeleteId")
    }

}
