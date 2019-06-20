package testoffset.tim.todos.intentions.add.commandParser

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tim.todo.intentions.ToDoData
import tim.todos.intentions.CommandParseError
import tim.todos.intentions.add.parseAddCommand

class ParseAddCommandSpek : Spek({
    describe("AddCommandParser") {
        describe("successfully parses the command") {
            it("can extract a title") {
                val expectedTitle = "some title"
                val expected = ToDoData(expectedTitle)

                when (val successOrFailure = parseAddCommand("ADD -t [$expectedTitle]")) {
                    is Either.Right -> {
                        assertThat(successOrFailure.b.data()).isEqualTo(expected)
                    }
                    is Either.Left -> fail("The response should be successful")
                }
            }

            it("can extract a description") {
                val expectedDescription = "some description"
                val expected = ToDoData("some title", expectedDescription)

                when (val successOrFailure =
                    parseAddCommand("ADD -t [some title] -d [$expectedDescription]")) {
                    is Either.Right -> {
                        assertThat(successOrFailure.b.data()).isEqualTo(expected)
                    }
                    is Either.Left -> fail("The response should be successful")
                }
            }
        }

        describe("error parsing the command") {
            it("gets an error if the title is missing") {
                when (val successOrFailure = parseAddCommand("ADD -d [some description]")) {
                    is Either.Right -> fail("It should not be successful if there is not title attribute")
                    is Either.Left -> assertThat(successOrFailure.a).isInstanceOf(CommandParseError::class.java)
                }
            }
        }
    }
})