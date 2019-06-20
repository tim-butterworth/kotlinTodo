package tim.todos.commandParsing.stateMachine

import java.util.*

class FinalState : StateMachineState {
    private val uuid: UUID = UUID.randomUUID()
    override fun id(): UUID = uuid
}
