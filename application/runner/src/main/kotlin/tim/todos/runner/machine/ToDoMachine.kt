package tim.todos.runner.machine

import tim.todos.runner.listener.CommandListener

class ToDoMachine(
    private val threads: List<Thread>,
    private val streams: List<AutoCloseable>,
    private val commandListener: CommandListener
) {
    fun startMachine() {
        val listenerThread = Thread { commandListener.start() }

        threads.forEach { it.start() }
        listenerThread.start()

        threads.forEach { it.join() }
        listenerThread.join()

        streams.forEach { it.close() }
    }
}
