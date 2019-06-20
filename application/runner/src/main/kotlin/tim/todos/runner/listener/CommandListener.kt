package tim.todos.runner.listener

import tim.todos.runner.machine.ExitableRunnableFactory
import tim.todos.runner.router.CommandRouter
import java.util.concurrent.BlockingQueue

class CommandListener(
    private val inQueue: BlockingQueue<String>,
    private val exitableRunnableFactory: ExitableRunnableFactory,
    private val outputQueues: List<BlockingQueue<String>>,
    private val commandHandler: CommandRouter
) {
    fun start() {
        var running = true
        while (running) {
            when (val input = inQueue.take()) {
                exitableRunnableFactory.EXIT -> {
                    outputQueues.forEach { queue ->
                        queue.put(exitableRunnableFactory.EXIT)
                    }

                    running = false
                }
                else -> commandHandler(input)
            }
        }
    }
}