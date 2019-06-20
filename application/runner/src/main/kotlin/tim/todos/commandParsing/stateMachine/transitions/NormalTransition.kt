package tim.todos.commandParsing.stateMachine.transitions

import tim.todos.commandParsing.stateMachine.StateMachineState

class NormalTransition<T>(
    private val transitionPredicate: (T) -> Boolean,
    private val nextState: StateMachineState
) : StateMachineTransition<T> {
    override fun shouldTransition(t: T): Boolean = transitionPredicate(t)
    override fun nextState(): StateMachineState = nextState
    override fun executeTransitionOperation(t: T) {}
}
