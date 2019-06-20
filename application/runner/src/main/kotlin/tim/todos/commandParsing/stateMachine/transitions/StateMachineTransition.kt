package tim.todos.commandParsing.stateMachine.transitions

import tim.todos.commandParsing.stateMachine.StateMachineState

interface StateMachineTransition<T> {
    fun shouldTransition(t: T): Boolean
    fun nextState(): StateMachineState
    fun executeTransitionOperation(t: T)
}
