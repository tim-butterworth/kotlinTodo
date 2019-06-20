package tim.todos.intentions.delete

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import tim.todo.intentions.Command
import tim.todos.commandParsing.StringExtractor
import tim.todos.commandParsing.stateMachine.*
import tim.todos.commandParsing.stateMachine.executor.processInput
import tim.todos.commandParsing.stateMachine.transitions.NormalTransition
import tim.todos.commandParsing.stateMachine.transitions.RecordingTransition
import tim.todos.commandParsing.stateMachine.transitions.StateMachineTransition
import tim.todos.intentions.CommandParseError
import java.util.*

private val error = ErrorState
private val start = StartState
private val firstLetterMatch = IntermediateState()
private val secondLetterMatch = IntermediateState()
private val thirdLetterMatch = IntermediateState()
private val fourthLetterMatch = IntermediateState()
private val fifthLetterMatch = IntermediateState()
private val correctCommandSymbol = IntermediateState()
private val argument = IntermediateState()
private val prepareToGatherArgument = FinalState()
private val gatherArgument = IntermediateState()

fun parseDeleteCommand(command: String): Either<CommandParseError, Command<UUID>> {
    val idExtractor = StringExtractor()

    extractDeleteCommand(command, idExtractor)

    return idExtractor.getData().flatMap<UUID> { extractedId: StringBuffer ->
        try {
            Option.just(UUID.fromString(extractedId.toString()))
        } catch (e: IllegalArgumentException) {
            Option.empty()
        }
    }.map { uuid ->
        val deleteCommand = object : Command<UUID> {
            override fun data(): UUID = uuid
        }
        Either.right(deleteCommand)
    }.getOrElse { Either.left(object : CommandParseError {}) }
}

private fun extractDeleteCommand(command: String, idExtractor: StringExtractor) {
    val stateTransitionMap: Map<StateMachineState, List<StateMachineTransition<Char>>> = mapOf(
        Pair(error, listOf(NormalTransition({ true }, error)))
        , getExactMatch(
            'D',
            keyState = start,
            nextState = firstLetterMatch
        )
        , getExactMatch(
            'E',
            keyState = firstLetterMatch,
            nextState = secondLetterMatch
        )
        , getExactMatch(
            'L',
            keyState = secondLetterMatch,
            nextState = thirdLetterMatch
        )
        , getExactMatch(
            'E',
            keyState = thirdLetterMatch,
            nextState = fourthLetterMatch
        )
        , getExactMatch(
            'T',
            keyState = fourthLetterMatch,
            nextState = fifthLetterMatch
        )
        , getExactMatch(
            'E',
            keyState = fifthLetterMatch,
            nextState = correctCommandSymbol
        )
        , Pair(
            correctCommandSymbol, listOf(
                NormalTransition({ c: Char -> c == ' ' }, correctCommandSymbol)
                , NormalTransition({ c: Char -> c == '-' }, argument)
            )
        )
        , Pair(
            argument, listOf(
                NormalTransition({ c: Char -> c == 'i' }, prepareToGatherArgument)
            )
        )
        , Pair(
            prepareToGatherArgument, listOf(
                NormalTransition({ c: Char -> c == ' ' }, prepareToGatherArgument)
                , NormalTransition({ c: Char -> c == '[' }, gatherArgument)
            )
        )
        , Pair(
            gatherArgument, listOf(
                RecordingTransition(
                    updateFunction = { c: Char -> idExtractor.retain(c) },
                    nextState = gatherArgument,
                    transitionPredicate = { c: Char -> c != ']' }
                )
                , NormalTransition({ c: Char -> c == ']' }, prepareToGatherArgument)
            )
        )
    )

    processInput(stateTransitionMap, command.toList())
}

private fun getExactMatch(
    matchChar: Char,
    keyState: StateMachineState,
    nextState: StateMachineState
): Pair<StateMachineState, List<StateMachineTransition<Char>>> {
    return Pair(
        keyState, listOf(
            NormalTransition({ c: Char -> c == matchChar }, nextState)
        )
    )
}
