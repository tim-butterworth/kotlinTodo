package tim.todos.commandParsing.stateMachine

import java.util.*

object StartState : StateMachineState {
    private val uuid = UUID.randomUUID()
    override fun id(): UUID = uuid
}
