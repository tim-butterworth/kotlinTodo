package testoffset.tim.todos.intentions.add.testFactories

import arrow.core.Either
import testoffset.tim.todos.intentions.TestFactory
import tim.todo.intentions.ToDoData
import tim.todo.intentions.WriteFailure
import tim.todo.intentions.add.ToDoWriter
import java.util.*

class AddToDoWriterTestFactory(
    private val response: Either<WriteFailure, UUID> = Either.right(UUID.randomUUID())
) : TestFactory<ToDoWriter> {
    override fun getInstance(): ToDoWriter {
        return object : ToDoWriter {
            override fun write(todoData: ToDoData): Either<WriteFailure, UUID> {
                return response
            }
        }
    }
}
