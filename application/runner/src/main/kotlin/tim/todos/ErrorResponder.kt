package tim.todos

import tim.todos.intentions.MessageDispatcher

class ErrorResponder(private val errorDispatcher: MessageDispatcher<String>) {
    fun invalidCommand(input: String) {
        errorDispatcher("INVALID COMMAND -> [$input] is not a valid command")
    }

    fun missingRequirementsCommand(input: String, requirements: List<String>) {
        errorDispatcher("INVALID COMMAND -> [$input] ${requirements.joinToString(" ")}")
    }
}
