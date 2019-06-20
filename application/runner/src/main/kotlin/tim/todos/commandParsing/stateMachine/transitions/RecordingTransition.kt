package tim.todos.commandParsing.stateMachine.transitions

import tim.todos.commandParsing.stateMachine.StateMachineState

class RecordingTransition<T>(
    private val updateFunction: (T) -> Unit,
    private val nextState: StateMachineState,
    private val transitionPredicate: (T) -> Boolean
) : StateMachineTransition<T> {
    override fun shouldTransition(t: T): Boolean = transitionPredicate(t)
    override fun nextState(): StateMachineState = nextState
    override fun executeTransitionOperation(t: T) {
        updateFunction(t)
    }
}
