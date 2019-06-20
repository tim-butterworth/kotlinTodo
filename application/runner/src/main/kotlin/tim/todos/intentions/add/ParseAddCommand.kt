package tim.todos.intentions.add

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.orElse
import tim.todo.intentions.Command
import tim.todo.intentions.ToDoData
import tim.todo.intentions.add.AddToDoCommand
import tim.todos.intentions.CommandParseError
import tim.todos.commandParsing.StringExtractor
import tim.todos.commandParsing.stateMachine.*
import tim.todos.commandParsing.stateMachine.executor.processInput
import tim.todos.commandParsing.stateMachine.transitions.NormalTransition
import tim.todos.commandParsing.stateMachine.transitions.RecordingTransition
import tim.todos.commandParsing.stateMachine.transitions.StateMachineTransition

private val start = StartState
private val error = ErrorState
private val startGettingData = FinalState()

private val firstLetterMatch = IntermediateState()
private val secondLetterMatch = IntermediateState()
private val titleOrDescription = IntermediateState()

private val startReadingTitle = IntermediateState()
private val gatherTitle = IntermediateState()

private val startReadingDescription = IntermediateState()
private val gatherDescription = IntermediateState()

fun parseAddCommand(command: String): Either<CommandParseError, Command<ToDoData>> {
    val titleExtractor = StringExtractor()
    val descriptionExtractor = StringExtractor()
    extractAddCommandData(command, titleExtractor, descriptionExtractor)

    return titleExtractor.getData()
        .flatMap { title ->
            descriptionExtractor.getData()
                .map { description ->
                    ToDoData(
                        title.toString(),
                        description.toString()
                    )
                }.orElse { Option.just(ToDoData(title.toString())) }
                .map { Either.right(AddToDoCommand(it)) }
        }.getOrElse {
            Either.left(object : CommandParseError {})
        }
}

private fun extractAddCommandData(
    command: String,
    titleExtractor: StringExtractor,
    descriptionExtractor: StringExtractor
) {

    val titleCharFoundTransition: StateMachineTransition<Char> = RecordingTransition(
        updateFunction = { c -> titleExtractor.retain(c) },
        nextState = gatherTitle,
        transitionPredicate = { c -> c != ']' }
    )
    val descriptionCharFoundTransition: StateMachineTransition<Char> = RecordingTransition(
        updateFunction = { c -> descriptionExtractor.retain(c) },
        nextState = gatherDescription,
        transitionPredicate = { c -> c != ']' }
    )

    val stateTransitionMap = mapOf(
        Pair(error, listOf(NormalTransition({ true }, error)))
        , Pair(
            start, listOf(NormalTransition({ c: Char -> c == 'A' },
                firstLetterMatch
            )))
        , Pair(
            firstLetterMatch, listOf(NormalTransition({ c: Char -> c == 'D' },
                secondLetterMatch
            )))
        , Pair(
            secondLetterMatch, listOf(NormalTransition({ c: Char -> c == 'D' },
                startGettingData
            )))
        , Pair(
            startGettingData,
            listOf(
                NormalTransition({ c: Char -> c == ' ' }, startGettingData)
                , NormalTransition({ c: Char -> c == '-' }, titleOrDescription)
            )
        )
        , Pair(
            titleOrDescription,
            listOf(
                NormalTransition({ c: Char -> c == 't' }, startReadingTitle)
                , NormalTransition({ c: Char -> c == 'd' }, startReadingDescription)
            )
        )
        , Pair(
            startReadingTitle,
            listOf(
                NormalTransition({ c: Char -> c == ' ' }, startReadingTitle)
                , NormalTransition({ c: Char -> c == '[' }, gatherTitle)
            )
        )
        , Pair(
            gatherTitle,
            listOf(
                NormalTransition({ c: Char -> c == ']' }, startGettingData)
                , titleCharFoundTransition
            )
        )
        , Pair(
            startReadingDescription,
            listOf(
                NormalTransition({ c: Char -> c == ' ' }, startReadingDescription)
                , NormalTransition({ c: Char -> c == '[' }, gatherDescription)
            )
        )
        , Pair(
            gatherDescription,
            listOf(
                NormalTransition({ c: Char -> c == ']' }, startGettingData)
                , descriptionCharFoundTransition
            )
        )
    )

    processInput(stateTransitionMap, command.toList())
}