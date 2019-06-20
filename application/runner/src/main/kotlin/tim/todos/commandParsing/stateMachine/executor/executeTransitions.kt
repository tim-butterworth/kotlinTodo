package tim.todos.commandParsing.stateMachine.executor

import tim.todos.commandParsing.stateMachine.ErrorState
import tim.todos.commandParsing.stateMachine.StartState
import tim.todos.commandParsing.stateMachine.StateMachineState
import tim.todos.commandParsing.stateMachine.transitions.StateMachineTransition

private val start = StartState
private val error = ErrorState

fun <T> processInput(
    stateTransitionMap: Map<StateMachineState, List<StateMachineTransition<T>>>,
    inputString: List<T>
): StateMachineState {
    var currentState: StateMachineState = start
    inputString.forEach { c ->
        currentState = stateMachineState(stateTransitionMap, currentState, c)
    }
    return currentState
}

private fun <T> stateMachineState(
    stateTransitionMap: Map<StateMachineState, List<StateMachineTransition<T>>>,
    currentState: StateMachineState,
    c: T
): StateMachineState {
    val transitions = stateTransitionMap.getOrDefault(currentState, emptyList())

    val iterator = transitions.iterator()

    while (iterator.hasNext()) {
        val transition = iterator.next()

        if (transition.shouldTransition(c)) {
            transition.executeTransitionOperation(c)

            return transition.nextState()
        }
    }

    return error
}