package tim.todos.intentions

interface CommandHandler {
    fun handlesCommand(command: String): Boolean
    fun handle(command: String)
}
