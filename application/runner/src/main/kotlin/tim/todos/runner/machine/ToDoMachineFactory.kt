package tim.todos.runner.machine

import tim.todos.runner.listener.CommandListener
import tim.todos.runner.router.CommandRouterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class ToDoMachineFactory {
    fun getInstance(streamsProvider: StreamsProvider): ToDoMachine {
        val systemIn = BufferedReader(InputStreamReader(streamsProvider.getInputStream()))
        val outWriter = ImmediateWriter(OutputStreamWriter(streamsProvider.getNormalOutputStream()))
        val errorWriter = ImmediateWriter(OutputStreamWriter(streamsProvider.getErrorOutputStream()))

        val inQueue: BlockingQueue<String> = LinkedBlockingQueue()
        val outQueue: BlockingQueue<String> = LinkedBlockingQueue()
        val errorQueue: BlockingQueue<String> = LinkedBlockingQueue()

        val outputQueues = listOf(outQueue, errorQueue)
        val streams = listOf(systemIn, outWriter, errorWriter)

        val exitableRunnableFactory = ExitableRunnableFactory()

        val inOutThreads = listOf(
            Thread(
                exitableRunnableFactory.getInstance(
                    { systemIn.readLine() },
                    { line -> line == ":exit" },
                    { line -> inQueue.put(line) },
                    { inQueue.put(exitableRunnableFactory.EXIT) })
            ),
            Thread(
                exitableRunnableFactory.getInstance(
                    { outQueue.take() },
                    { line -> line == exitableRunnableFactory.EXIT },
                    { line -> outWriter.write(line) },
                    { outWriter.write("\nSUTTING DOWN [standard stream]\n") }
                )
            ),
            Thread(
                exitableRunnableFactory.getInstance(
                    { errorQueue.take() },
                    { line -> line == exitableRunnableFactory.EXIT },
                    { line -> errorWriter.write("[ERROR] -> $line") },
                    { errorWriter.write("\nSHUTTING DOWN [error stream]\n") }
                )
            )
        )

        val commandHandler = CommandRouterFactory().getInstance(outQueue, errorQueue)

        return ToDoMachine(
            inOutThreads,
            streams,
            CommandListener(
                inQueue,
                exitableRunnableFactory,
                outputQueues,
                commandHandler
            )
        )
    }

}
