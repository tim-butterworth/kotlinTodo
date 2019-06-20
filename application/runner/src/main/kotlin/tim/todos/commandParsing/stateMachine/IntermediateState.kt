package tim.todos.commandParsing.stateMachine

import java.util.*

class IntermediateState : StateMachineState {
    private val uuid = UUID.randomUUID()
    override fun id(): UUID = uuid
}
