package tim.todos.commandParsing.stateMachine

import java.util.*

object ErrorState : StateMachineState {
    private val uuid = UUID.randomUUID()
    override fun id(): UUID = uuid
}