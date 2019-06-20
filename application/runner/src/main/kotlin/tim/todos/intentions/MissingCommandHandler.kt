package tim.todos.intentions

import tim.todos.ErrorResponder

class MissingCommandHandler(private val errorResponder: ErrorResponder) : CommandHandler {
    override fun handlesCommand(command: String): Boolean = true
    override fun handle(command: String) {
        errorResponder.invalidCommand(command)
    }
}