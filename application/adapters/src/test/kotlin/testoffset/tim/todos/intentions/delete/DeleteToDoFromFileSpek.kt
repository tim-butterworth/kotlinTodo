package testoffset.tim.todos.intentions.delete

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import com.fasterxml.jackson.databind.ObjectMapper
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testoffset.tim.todos.intentions.testFileHelpers.TestFiles
import testoffset.tim.todos.intentions.testFileHelpers.withTemporaryFile
import tim.todo.intentions.ToDo
import tim.todo.intentions.delete.DeleteFailure
import tim.todo.intentions.delete.DeleteSuccess
import tim.todo.intentions.delete.IOError
import tim.todo.intentions.delete.ToDoNotFound
import tim.todos.intentions.ToDosContainer
import tim.todos.intentions.delete.ToDoDeleterFactory
import java.util.*

class DeleteToDoFromFileSpek : Spek({
    describe("deleteToDo") {
        describe("file does not exist") {
            it("returns an error response") {
                val toDoDeleter = ToDoDeleterFactory().getInstance("doesnotexist")
                val actualDeletionResponse = toDoDeleter.deleteToDo(UUID.randomUUID())

                actualDeletionResponse.map { fail("Should not be a success") }
                actualDeletionResponse.mapLeft { assertThat(it).isEqualTo(IOError) }
            }
        }

        describe("todo does not exist") {
            it("returns an error response") {
                val todosString = ObjectMapper().writeValueAsString(
                    ToDosContainer(
                        listOf(
                            ToDo(UUID.randomUUID(), "title", "description")
                        )
                    )
                )

                TestFiles().withTemporaryFile(todosString) { filePath ->
                    val toDoDeleter = ToDoDeleterFactory().getInstance(filePath)
                    val actualDeletionResponse = toDoDeleter.deleteToDo(UUID.randomUUID())

                    todoNotFoundExpectation(actualDeletionResponse)
                }
            }
        }

        describe("successfully deletes a todo") {
            it("returns a success response") {
                val idToDelete = UUID.randomUUID()
                val todosString = ObjectMapper().writeValueAsString(
                    ToDosContainer(
                        listOf(
                            ToDo(idToDelete, "title", "description")
                        )
                    )
                )

                TestFiles().withTemporaryFile(todosString) { filePath ->
                    val toDoDeleter = ToDoDeleterFactory().getInstance(filePath)
                    val actualDeleteResponse = toDoDeleter.deleteToDo(idToDelete)

                    deleteSuccessExpectations(actualDeleteResponse)
                }
            }

            it("can not delete each of multiple todos") {
                val ids = (0..10).map { UUID.randomUUID() }

                val todosString = ObjectMapper().writeValueAsString(
                    ToDosContainer(ids.map { id -> ToDo(id = id, title = "title", description = "description") })
                )

                TestFiles().withTemporaryFile(todosString) { filePath ->
                    val toDoDeleter = ToDoDeleterFactory().getInstance(filePath)

                    ids.forEach { id ->
                        deleteSuccessExpectations(toDoDeleter.deleteToDo(id))
                    }

                    ids.forEach { id ->
                        todoNotFoundExpectation(toDoDeleter.deleteToDo(id))
                    }
                }
            }
        }
    }
})

private fun todoNotFoundExpectation(actualDeletionResponse: Either<DeleteFailure, DeleteSuccess>) {
    actualDeletionResponse.map { fail("Should not be success") }
    actualDeletionResponse.mapLeft { assertThat(it).isEqualTo(ToDoNotFound) }
}

private fun deleteSuccessExpectations(actualDeleteResponse: Either<DeleteFailure, DeleteSuccess>) {
    actualDeleteResponse.mapLeft { fail("Should not be a failure") }
    actualDeleteResponse.mapLeft { assertThat(it).isEqualTo(DeleteSuccess()) }
}