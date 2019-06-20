package tim.todos.intentions.delete

import arrow.core.Either
import tim.todo.intentions.Command
import tim.todo.intentions.delete.DeleteToDo
import tim.todos.intentions.CommandHandler
import tim.todos.intentions.CommandParseError
import tim.todos.intentions.MessageDispatcher
import java.util.*

class DeleteToDoCommandHandler(
    private val deleteToDo: DeleteToDo<Unit>,
    private val errorQueue: MessageDispatcher<String>,
    private val commandParser: (String) -> Either<CommandParseError, Command<UUID>>
) : CommandHandler {
    override fun handlesCommand(command: String): Boolean = command.startsWith("DELETE")
    override fun handle(command: String) {
        val commandParseResult = commandParser(command)

        commandParseResult.map { success -> deleteToDo.execute(success.data()) }
        commandParseResult.mapLeft { errorQueue.invoke("Error deleting stuff") }
    }
}
