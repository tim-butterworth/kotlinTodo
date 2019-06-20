package testoffset.tim.todos.intentions.add.testFactories

import testoffset.tim.todos.intentions.TestFactory
import tim.todo.intentions.WriteFailure
import tim.todo.intentions.add.AddToDoResponder
import java.util.*

class AddToDoResponderTestFactory(
    private val successFn: (UUID) -> Unit = { _ -> },
    private val errorFn: (WriteFailure) -> Unit = { _ -> }
) : TestFactory<AddToDoResponder<Unit>> {
    override fun getInstance(): AddToDoResponder<Unit> {
        return object : AddToDoResponder<Unit> {
            override fun success(id: UUID): Unit = successFn(id)
            override fun error(failure: WriteFailure): Unit = errorFn(failure)
        }
    }
}
