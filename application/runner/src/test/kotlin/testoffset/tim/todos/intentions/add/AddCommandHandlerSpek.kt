package testoffset.tim.todos.intentions.add

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testoffset.tim.todos.intentions.TestFactory
import testoffset.tim.todos.intentions.add.testFactories.AddCommandHandlerTestFactory
import testoffset.tim.todos.intentions.add.testFactories.AddCommandParserTestFactory
import testoffset.tim.todos.intentions.add.testFactories.AddToDoResponderTestFactory
import testoffset.tim.todos.intentions.add.testFactories.AddToDoTestFactory
import tim.todo.intentions.Command
import tim.todo.intentions.ToDoData
import tim.todo.intentions.WriteFailure
import tim.todo.intentions.add.AddToDo
import tim.todo.intentions.add.AddToDoCommand
import tim.todo.intentions.add.AddToDoResponder
import tim.todo.intentions.add.ToDoWriter
import tim.todos.intentions.CommandParseError
import tim.todos.intentions.MessageDispatcher
import tim.todos.intentions.add.AddCommandHandler
import java.util.*

class AddCommandHandlerSpek : Spek({
    describe("handle") {
        describe("success") {
            it("dispatches a success message if there is a title") {
                val expectedId = UUID.randomUUID()
                val successQueue = LinkedList<String>()
                val errorQueue = LinkedList<String>()
                val dataToWrite = LinkedList<ToDoData>()

                val addCommandHandlerTestFactory =
                    AddCommandHandlerTestFactory(
                        messageDispatcherFactory = getErrorDispatcherTestFactory(errorQueue),
                        addCommandParserFactory = AddCommandParserTestFactory(
                            parser = {
                                Either.right(AddToDoCommand(ToDoData(title = "fancy title")))
                            }),
                        addToDoFactory = AddToDoTestFactory(
                            addToDoResponderTestFactory = AddToDoResponderTestFactory(
                                successFn = { message -> successQueue.add(message.toString()) }
                            ),
                            toDoWriterTestFactory = object :
                                TestFactory<ToDoWriter> {
                                override fun getInstance(): ToDoWriter =
                                    object : ToDoWriter {
                                        override fun write(todoData: ToDoData): Either<WriteFailure, UUID> {
                                            dataToWrite.add(todoData)
                                            return Either.right(expectedId)
                                        }
                                    }
                            }
                        )
                    )
                val addCommandHandler = addCommandHandlerTestFactory.getInstance()

                addCommandHandler.handle("ADD -t [fancy title]")

                assertThat(errorQueue).isEmpty()
                assertThat(successQueue).isNotEmpty()
                assertThat(successQueue.remove()).isEqualTo(expectedId.toString())
                assertThat(dataToWrite).isNotEmpty()
                assertThat(dataToWrite).hasSize(1)
                assertThat(dataToWrite.remove()).isEqualTo(ToDoData("fancy title"))
            }
        }

        describe("failure") {
            it("dispatches an error message if command is invalid") {
                val successQueue = LinkedList<String>()
                val errorQueue = LinkedList<String>()

                val addCommandHandlerTestFactory =
                    AddCommandHandlerTestFactory(
                        addCommandParserFactory = AddCommandParserTestFactory(
                            parser = {
                                Either.left(object : CommandParseError {})
                            }),
                        addToDoFactory = AddToDoTestFactory(
                            addToDoResponderTestFactory = AddToDoResponderTestFactory(
                                successFn = { message -> successQueue.add(message.toString()) },
                                errorFn = { message -> errorQueue.add(message.toString()) }
                            ),
                            toDoWriterTestFactory = getExplodingWriterTestFactory()
                        ),
                        messageDispatcherFactory = getErrorDispatcherTestFactory(errorQueue)
                    )
                val addCommandHandler = addCommandHandlerTestFactory.getInstance()

                addCommandHandler.handle("ADD_COMMAND")

                assertThat(successQueue).isEmpty()
                assertThat(errorQueue).isNotEmpty()
                assertThat(errorQueue.remove()).isNotEmpty()
            }
        }
    }

    describe("handlesCommand") {
        fun getNoOpAddCommand(): AddCommandHandler<String> {
            val writer = object : ToDoWriter {
                override fun write(todoData: ToDoData): Either<WriteFailure, UUID> = Either.right(UUID.randomUUID())
            }
            val responder = object : AddToDoResponder<String> {
                override fun success(id: UUID): String = ""
                override fun error(failure: WriteFailure): String = ""
            }
            return AddCommandHandler(AddToDo(writer, responder), object : MessageDispatcher<String> {
                override fun invoke(message: String) {}
            }) {
                Either.right(object : Command<ToDoData> {
                    override fun data(): ToDoData = ToDoData("")
                })
            }
        }

        it("returns true for ADD command") {
            assertThat(getNoOpAddCommand().handlesCommand("ADD and other stuff")).isEqualTo(true)
        }

        it("returns false for ADD command") {
            assertThat(getNoOpAddCommand().handlesCommand("NOT_ADD")).isEqualTo(false)
        }
    }
})

private fun getErrorDispatcherTestFactory(errorQueue: LinkedList<String>): TestFactory<MessageDispatcher<String>> {
    return object : TestFactory<MessageDispatcher<String>> {
        override fun getInstance(): MessageDispatcher<String> {
            return object : MessageDispatcher<String> {
                override fun invoke(message: String) {
                    errorQueue.add(message)
                }
            }
        }
    }
}

private fun getExplodingWriterTestFactory(): TestFactory<ToDoWriter> {
    return object : TestFactory<ToDoWriter> {
        override fun getInstance(): ToDoWriter =
            object : ToDoWriter {
                override fun write(todoData: ToDoData): Either<WriteFailure, UUID> {
                    fail("The writer should not be called if the command is invalid")
                }
            }
    }
}
