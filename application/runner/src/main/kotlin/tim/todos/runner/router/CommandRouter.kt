package tim.todos.runner.router

import tim.todos.intentions.CommandHandler

class CommandRouter(private val handlers: List<CommandHandler>) {
    operator fun invoke(input: String) {
        var handled = false

        val indexRange = (0..handlers.size).iterator()

        while(!handled && indexRange.hasNext()) {
            val handler = handlers[indexRange.nextInt()]
            if (handler.handlesCommand(input)) {
                handler.handle(input)
                handled = true
            }
        }
    }
}