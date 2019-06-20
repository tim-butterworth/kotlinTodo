package testoffset.tim.todo.intentions.readAll

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testoffset.helpers.getRandomBetween
import tim.todo.intentions.*
import tim.todo.intentions.readAll.AllToDos
import tim.todo.intentions.readAll.AllToDoProvider
import tim.todo.intentions.readAll.AllToDoResponder
import java.util.*

class AllTodosSpek : Spek({
    describe("DisplayTodos") {
        describe("execute") {
            describe("when successful") {
                it("responds with todos") {
                    val expected = mutableListOf<ToDo>()

                    (1..getRandomBetween(minimum = 4, maximum = 24)).forEach { _ ->
                        expected.add(ToDo(UUID.randomUUID(), title = "title", description = "description"))
                    }
                    val todoProvider = object : AllToDoProvider {
                        override fun getAll(): Either<ReadFailure, List<ToDo>> = Either.right(expected)
                    }
                    val todoResponder = object : AllToDoResponder<List<UUID>> {
                        override fun success(todos: List<ToDo>) = todos.map { it.id }
                        override fun error(failure: ReadFailure): List<UUID> = fail("error should not be called")
                    }

                    val actual = AllToDos(todoProvider, todoResponder).execute()

                    assertThat(actual).hasSize(expected.size)
                    assertThat(actual).isEqualTo(expected.map { todo -> todo.id })
                }
            }

            describe("when not successful") {
                it("responds with an error response") {
                    val todoProvider = object : AllToDoProvider {
                        override fun getAll(): Either<ReadFailure, List<ToDo>> = Either.left(ReadFailure(
                            ReadFailureReason.MISSING_FILE,
                            "message"
                        ))
                    }
                    val todoResponder = object : AllToDoResponder<String> {
                        override fun error(failure: ReadFailure): String = "Error response"
                        override fun success(todos: List<ToDo>) = fail("success should not be called")
                    }

                    val actual = AllToDos(todoProvider, todoResponder).execute()

                    assertThat(actual).isEqualTo("Error response")
                }
            }
        }
    }
})
