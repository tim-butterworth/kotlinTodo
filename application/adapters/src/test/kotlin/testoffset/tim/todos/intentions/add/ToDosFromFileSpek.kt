package testoffset.tim.todos.intentions.add

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.fail
import com.fasterxml.jackson.databind.ObjectMapper
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testoffset.tim.todos.intentions.testFileHelpers.TestFiles
import testoffset.tim.todos.intentions.testFileHelpers.withTemporaryFile
import tim.todo.intentions.ReadFailureReason
import tim.todo.intentions.ToDo
import tim.todos.intentions.readAll.AllToDoProviderFactory
import tim.todos.intentions.ToDosContainer
import java.util.*

class ToDosFromFileSpek : Spek({
    describe("getAll") {
        val allToDoProviderFactory = AllToDoProviderFactory()

        describe("when the file exists") {
            describe("and is empty") {
                it("returns a ReadFailure response") {
                    val toDosFromFile =
                        allToDoProviderFactory.withAbsoluteFileAccessor(this.javaClass.classLoader.getResource("emptyfile.json").file)

                    val actual = toDosFromFile.getAll().fold({ it }, { fail("Should be an error response") })

                    assertThat(actual.readFailureReason).isEqualTo(ReadFailureReason.INVALID_FILE)
                    assertThat(actual.message).isNotNull()
                }
            }

            describe("and contains valid todos") {
                it("returns a populated list of todos") {
                    val todos = listOf(
                        getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                        , getToDo()
                    )
                    val objectMapper = ObjectMapper()
                    val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()
                    val fileContent = objectWriter.writeValueAsString(ToDosContainer(todos))

                    TestFiles().withTemporaryFile(fileContent) { filePath ->
                        val toDosFromFile = AllToDoProviderFactory().withAbsoluteFileAccessor(filePath)
                        val actual = toDosFromFile.getAll()

                        assertThat(actual).isEqualTo(Either.right(todos))
                    }
                }
            }

            describe("and contains invalid keys") {
                it("returns a ReadFailure response") {
                    val fileContent = "{\"not_expected\": [{\"bad_key\":\"value\"}]}"

                    TestFiles().withTemporaryFile(fileContent) { filePath ->
                        val toDosFromFile = allToDoProviderFactory.withAbsoluteFileAccessor(filePath)

                        val actual = toDosFromFile.getAll().fold({ it }, { fail("Should be an error response") })

                        assertThat(actual.readFailureReason).isEqualTo(ReadFailureReason.INVALID_FILE)
                        assertThat(actual.message).isNotNull()
                    }
                }
            }

            describe("and contains invalid JSON") {
                it("returns a ReadFailure response") {
                    val fileContent = "{ not_expected: [{\"bad_key\":\"value\"}]}"

                    TestFiles().withTemporaryFile(fileContent) { filePath ->
                        val toDosFromFile = allToDoProviderFactory.withAbsoluteFileAccessor(filePath)

                        val actual = toDosFromFile.getAll().fold({ it }, { fail("Should be an error response") })

                        assertThat(actual.readFailureReason).isEqualTo(ReadFailureReason.INVALID_FILE)
                        assertThat(actual.message).isNotNull()
                    }
                }
            }
        }

        describe("when the file does not exist") {
            it("returns a ReadFailure response") {
                val toDosFromFile = allToDoProviderFactory.withAbsoluteFileAccessor("doesnotexist")

                val actual = toDosFromFile.getAll().fold({ it }, { fail("Should be an error response") })

                assertThat(actual.readFailureReason).isEqualTo(ReadFailureReason.MISSING_FILE)
                assertThat(actual.message).isNotNull()
            }
        }
    }
})

private fun getToDo() = ToDo(
    id = UUID.randomUUID(),
    title = "title",
    description = "description"
)