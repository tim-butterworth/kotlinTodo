package tim.todos.intentions.readAll

import tim.todo.intentions.readAll.AllToDos
import tim.todos.intentions.CommandHandler
import tim.todos.intentions.MessageDispatcher

class AllToDoCommandHandler(
    private val allToDos: AllToDos<Unit>,
    private val errorMessageDispatcher: MessageDispatcher<String>
) : CommandHandler {
    override fun handlesCommand(command: String): Boolean = "ALL" == command
    override fun handle(command: String) {
        allToDos.execute()
    }
}