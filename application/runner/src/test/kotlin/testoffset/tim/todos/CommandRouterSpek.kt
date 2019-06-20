package testoffset.tim.todos

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tim.todo.intentions.*
import tim.todo.intentions.add.AddToDo
import tim.todo.intentions.add.ToDoWriter
import tim.todo.intentions.readAll.AllToDoProvider
import tim.todo.intentions.readAll.AllToDos
import tim.todos.runner.router.CommandRouter
import tim.todos.ErrorResponder
import tim.todos.intentions.MessageDispatcher
import tim.todos.intentions.MissingCommandHandler
import tim.todos.intentions.add.AddCommandHandler
import tim.todos.intentions.add.AddToDoResponderCommandLine
import tim.todos.intentions.add.parseAddCommand
import tim.todos.intentions.readAll.AllToDoCommandHandler
import tim.todos.intentions.readAll.AllToDoResponderCommandLine
import java.util.*

class CommandRouterSpek : Spek({
    describe("invoke") {
        describe("for valid commands") {
            describe("[ALL] success") {
                it("displays all todos") {
                    val todos = listOf(
                        ToDo(
                            UUID.randomUUID(),
                            title = "title1",
                            description = "description1"
                        ),
                        ToDo(
                            UUID.randomUUID(),
                            title = "title2",
                            description = "description2"
                        )
                    )

                    withSetup(allToDoResponse = Either.right(todos)) { commandRouter, successQueue, errorQueue ->
                        commandRouter("ALL")

                        assertThat(successQueue).isNotEmpty()
                        assertThat(errorQueue).isEmpty()

                        val queueStrings = extractQueueToList(successQueue)

                        assertThat(queueStrings).hasSize(todos.size)

                        queueStrings.forEachIndexed { index, value ->
                            assertThat(value).contains(todos[index].id.toString())
                        }
                    }
                }
            }

            describe("[ALL] failure") {
                it("displays the read error") {
                    withSetup(
                        allToDoResponse = Either.left(
                            ReadFailure(ReadFailureReason.INVALID_FILE, "message")
                        )
                    ) { commandRouter, successQueue, errorQueue ->
                        commandRouter("ALL")

                        assertThat(successQueue).isEmpty()
                        assertThat(errorQueue).isNotEmpty()

                        val queueStrings = extractQueueToList(errorQueue)

                        assertThat(queueStrings).hasSize(1)
                        assertThat(queueStrings[0]).contains("INVALID_FILE")
                    }
                }
            }

            describe("[ADD] success") {
                it("responds with the id of the new ToDo") {
                    val expectedUUIDString = "b5bd19f6-b92d-4eb0-a9de-cf5560c55da8"

                    withSetup(
                        addToDoResponse = Either.right(UUID.fromString(expectedUUIDString)),
                        expectations = { commandRouter, successQueue, errorQueue ->
                            val title = "title"
                            val description = "description"
                            val input = "ADD -t [$title] -d [$description]"

                            commandRouter(input)

                            assertThat(successQueue).isNotEmpty()
                            assertThat(errorQueue).isEmpty()

                            val queueStrings = extractQueueToList(successQueue)

                            assertThat(queueStrings).hasSize(1)
                            assertThat(queueStrings[0]).isEqualTo(expectedUUIDString)
                        }
                    )
                }
            }

            describe("[ADD] failure") {
                it("displays the write error") {
                    withSetup(addToDoResponse = Either.left(WriteFailure())) { commandRouter, successQueue, errorQueue ->
                        commandRouter("ADD -t some-title -d some-description")

                        assertThat(successQueue).isEmpty()
                        assertThat(errorQueue).isNotEmpty()

                        val queryStrings = extractQueueToList(errorQueue)

                        assertThat(queryStrings).hasSize(1)
                        assertThat(queryStrings[0]).isEqualTo("INVALID COMMAND -> [ADD] requires a title")
                    }
                }
            }
        }

        describe("for invalid commands") {
            describe("for a command that does not exist") {
                it("Returns an error message") {
                    withSetup { commandRouter, successQueue, errorQueue ->
                        commandRouter("INVALID_COMMAND")

                        assertThat(errorQueue).isNotEmpty()
                        assertThat(successQueue).isEmpty()

                        val errorResponse = errorQueue.remove()

                        assertThat(errorResponse).isNotNull()
                        assertThat(errorResponse).isEqualTo("INVALID COMMAND -> [INVALID_COMMAND] is not a valid command")
                    }
                }
            }

            describe("badly formatted [ADD]") {
                it("Returns an error message") {
                    withSetup { commandRouter, successQueue, errorQueue ->
                        commandRouter("ADD")

                        assertThat(errorQueue).isNotEmpty()
                        assertThat(successQueue).isEmpty()

                        val errorResponse = errorQueue.remove()

                        assertThat(errorResponse).isEqualTo("INVALID COMMAND -> [ADD] requires a title")
                    }
                }
            }
        }
    }
})

fun withSetup(
    allToDoResponse: Either<ReadFailure, List<ToDo>> = Either.right(emptyList()),
    addToDoResponse: Either<WriteFailure, UUID> = Either.right(UUID.randomUUID()),
    expectations: (commandRouter: CommandRouter, successQueue: Queue<String>, errorQueue: Queue<String>) -> Unit
) {
    val successQueue: Queue<String> = LinkedList<String>()
    val errorQueue: Queue<String> = LinkedList<String>()

    val successMessageDispatcher = getNonBlockingMessageDispatcher(successQueue)
    val errorMessageDispatcher = getNonBlockingMessageDispatcher(errorQueue)

    val allResponder = AllToDoResponderCommandLine(successMessageDispatcher, errorMessageDispatcher)
    val addResponder = AddToDoResponderCommandLine(
        successDispatcher = successMessageDispatcher,
        errorDispatcher = errorMessageDispatcher
    )

    val commandRouter = getCommandRouter(
        errorMessageDispatcher,
        AllToDos(
            provider = object : AllToDoProvider {
                override fun getAll(): Either<ReadFailure, List<ToDo>> {
                    return allToDoResponse
                }
            },
            responder = allResponder
        ),
        AddToDo(
            object : ToDoWriter {
                override fun write(todoData: ToDoData): Either<WriteFailure, UUID> {
                    return addToDoResponse
                }
            },
            addResponder
        )
    )

    expectations(commandRouter, successQueue, errorQueue)
}

private fun extractQueueToList(queue: Queue<String>): List<String> {
    val queueStrings = mutableListOf<String>()
    while (queue.isNotEmpty()) {
        queueStrings.add(queue.remove())
    }
    return queueStrings
}

private fun getCommandRouter(
    errorMessageDispatcher: MessageDispatcher<String>,
    allToDos: AllToDos<Unit>,
    addToDo: AddToDo<Unit>
): CommandRouter {
    val commandHandlers = listOf(
        AddCommandHandler(addToDo, errorMessageDispatcher) { command -> parseAddCommand(command) },
        AllToDoCommandHandler(allToDos, errorMessageDispatcher),
        MissingCommandHandler(ErrorResponder(errorMessageDispatcher))
    )

    return CommandRouter(commandHandlers)
}

fun getNonBlockingMessageDispatcher(queue: Queue<String>): MessageDispatcher<String> {
    return object : MessageDispatcher<String> {
        override fun invoke(message: String) {
            queue.add(message)
        }
    }
}
