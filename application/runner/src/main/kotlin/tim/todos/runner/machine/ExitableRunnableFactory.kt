package tim.todos.runner.machine

import java.util.*

class ExitableRunnableFactory {

    val EXIT = "EXIT" + UUID.randomUUID().toString()

    fun getInstance(
        producer: () -> String,
        exitPredicate: (string: String) -> Boolean,
        repeatAction: (string: String) -> Unit,
        shutdownAction: () -> Unit
    ): () -> Unit {
        return {
            var keepGoing = true
            while (keepGoing) {
                val value = producer()

                if (exitPredicate(value)) {
                    keepGoing = false

                    shutdownAction()
                } else {
                    repeatAction(value)
                }
            }
        }
    }
}
