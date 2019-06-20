package testoffset.tim.todos.intentions.delete.commandParser

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tim.todos.intentions.delete.parseDeleteCommand
import java.util.*

class ParseDeleteCommandSpek : Spek({
    val expectedId = UUID.randomUUID()

    describe("success") {
        it("extracts a delete command") {
            when (val parseDeleteCommand = parseDeleteCommand("DELETE -i [$expectedId]")) {
                is Either.Right -> {
                    assertThat(parseDeleteCommand.b.data()).isEqualTo(expectedId)
                }
                is Either.Left -> fail("The response should be successful")
            }
        }
    }

    describe("failure") {
        describe("wrong command") {
            it("Returns a failure command") {
                when (val parseDeleteCommand = parseDeleteCommand("DELETELY -i [$expectedId]")) {
                    is Either.Right -> fail("The response should not be successful")
                    is Either.Left -> {
                        assertThat(parseDeleteCommand.a).isNotNull()
                    }
                }
            }
        }

        describe("invalid UUID") {
            it("Returns a failure command") {
                when (val parseDeleteCommand = parseDeleteCommand("DELETE -i [invalid-uuid]")) {
                    is Either.Right -> fail("The response should not be successful")
                    is Either.Left -> {
                        assertThat(parseDeleteCommand.a).isNotNull()
                    }
                }
            }
        }

        describe("missing id") {

        }
    }
})