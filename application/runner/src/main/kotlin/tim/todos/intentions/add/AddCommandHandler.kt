package tim.todos.intentions.add

import arrow.core.Either
import tim.todo.intentions.Command
import tim.todo.intentions.ToDoData
import tim.todo.intentions.add.AddToDo
import tim.todos.intentions.CommandHandler
import tim.todos.intentions.CommandParseError
import tim.todos.intentions.MessageDispatcher

class AddCommandHandler<T>(
    private val addToDo: AddToDo<T>,
    private val errorQueue: MessageDispatcher<String>,
    private val commandParser: (String) -> Either<CommandParseError, Command<ToDoData>>
) : CommandHandler {
    override fun handlesCommand(command: String): Boolean {
        return command.startsWith("ADD")
    }

    override fun handle(command: String) {
        when (val addCommandOrError = commandParser(command)) {
            is Either.Right -> addToDo.execute(addCommandOrError.b.data())
            is Either.Left -> errorQueue("INVALID COMMAND -> [ADD] requires a title")
        }
    }
}