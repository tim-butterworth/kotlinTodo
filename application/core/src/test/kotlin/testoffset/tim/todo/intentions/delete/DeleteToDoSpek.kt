package testoffset.tim.todo.intentions.delete

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tim.todo.intentions.delete.*
import java.util.*

class DeleteToDoSpek : Spek({
    describe("DeleteToDo") {
        describe("on successful deletion response") {
            val successDeleter = object : ToDoDeleter {
                override fun deleteToDo(uuid: UUID): Either<DeleteFailure, DeleteSuccess> =
                    Either.right(DeleteSuccess())
            }

            it("calls the success callback") {
                val expectedSuccessResponse = "success response" + Random().nextLong()

                val actualDeleteResult =
                    DeleteToDo(
                        successDeleter,
                        getSuccessResponder(expectedSuccessResponse)
                    ).execute(UUID.randomUUID())

                assertThat(actualDeleteResult).isEqualTo(expectedSuccessResponse)
            }
        }

        describe("on failure deletion response") {
            val failureDeleter = object : ToDoDeleter {
                override fun deleteToDo(uuid: UUID): Either<DeleteFailure, DeleteSuccess> = Either.left(ToDoNotFound)
            }

            it("calls the failure callback") {
                val expectedFailureResponse = "failure response" + Random().nextLong()

                val actualDeleteResult =
                    DeleteToDo(
                        failureDeleter,
                        getFailureResponder(expectedFailureResponse)
                    ).execute(UUID.randomUUID())

                assertThat(actualDeleteResult).isEqualTo(expectedFailureResponse)
            }
        }
    }
})

fun getSuccessResponder(successResponse: String): DeleteToDoResponder<String> =
    object : DeleteToDoResponder<String> {
        override fun success(): String = successResponse
        override fun error(failedToDeleteId: UUID): String = fail("Error should not be called")
    }

fun getFailureResponder(failureResponse: String): DeleteToDoResponder<String> =
    object : DeleteToDoResponder<String> {
        override fun success(): String = fail("Success should not be called")
        override fun error(failedToDeleteId: UUID): String = failureResponse
    }