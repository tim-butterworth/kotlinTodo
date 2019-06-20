package testoffset.tim.todo.intentions.add

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tim.todo.intentions.*
import tim.todo.intentions.add.AddToDo
import tim.todo.intentions.add.AddToDoResponder
import tim.todo.intentions.add.ToDoWriter
import java.util.*

class AddToDoSpek : Spek({
    describe("AddToDoSpek") {
        describe("execute") {
            describe("when successful") {
                it("responds with the id of the new ToDo") {
                    val expected = UUID.randomUUID()
                    val responder = object : AddToDoResponder<String> {
                        override fun success(id: UUID): String = id.toString()
                        override fun error(failure: WriteFailure): String = fail("error should not be called")
                    }
                    val writer = object: ToDoWriter {
                        override fun write(todoData: ToDoData): Either<WriteFailure, UUID> = Either.right(expected)
                    }

                    assertThat(AddToDo(writer, responder).execute(ToDoData("new title"))).isEqualTo(expected.toString())
                }
            }

            describe("when not successful") {
                it("responds with an error response") {
                    val responder = object : AddToDoResponder<String> {
                        override fun success(id: UUID): String = fail("success should not be called")
                        override fun error(failure: WriteFailure) = "Got an error!"
                    }
                    val writer = object: ToDoWriter {
                        override fun write(todoData: ToDoData): Either<WriteFailure, UUID> = Either.left(WriteFailure())
                    }

                    assertThat(AddToDo(writer, responder).execute(ToDoData("new title"))).isEqualTo("Got an error!")
                }
            }
        }
    }
})