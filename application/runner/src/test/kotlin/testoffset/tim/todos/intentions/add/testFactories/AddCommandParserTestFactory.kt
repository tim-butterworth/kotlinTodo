package testoffset.tim.todos.intentions.add.testFactories

import arrow.core.Either
import tim.todo.intentions.Command
import tim.todo.intentions.ToDoData
import tim.todos.intentions.CommandParseError
import tim.todos.intentions.add.parseAddCommand

class AddCommandParserTestFactory(
    private val parser: (String) -> Either<CommandParseError, Command<ToDoData>> = { string ->
        parseAddCommand(
            string
        )
    }
) {
    fun getInstance(): (String) -> Either<CommandParseError, Command<ToDoData>> {
        return parser
    }
}
