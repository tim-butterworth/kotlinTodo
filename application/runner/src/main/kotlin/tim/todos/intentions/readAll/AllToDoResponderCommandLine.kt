package tim.todos.intentions.readAll

import tim.todo.intentions.ReadFailure
import tim.todo.intentions.ToDo
import tim.todo.intentions.readAll.AllToDoResponder
import tim.todos.intentions.MessageDispatcher

class AllToDoResponderCommandLine(
    private val successDispatcher: MessageDispatcher<String>,
    private val errorDispatcher: MessageDispatcher<String>
) : AllToDoResponder<Unit> {

    override fun success(todos: List<ToDo>) {
        todos.forEach { todo -> successDispatcher.invoke(todo.toString()) }
    }

    override fun error(failure: ReadFailure) {
        errorDispatcher(failure.toString())
    }

}