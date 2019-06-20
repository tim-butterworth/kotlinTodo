package tim.todo.intentions.readAll

import tim.todo.intentions.ReadFailure
import tim.todo.intentions.ToDo

interface AllToDoResponder<T> {
    fun success(todos: List<ToDo>): T
    fun error(failure: ReadFailure): T
}
