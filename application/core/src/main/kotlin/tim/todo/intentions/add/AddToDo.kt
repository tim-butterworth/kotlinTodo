package tim.todo.intentions.add

import tim.todo.intentions.ToDoData

class AddToDo<T>(private val todoWriter: ToDoWriter, private val responder: AddToDoResponder<T>) {
    fun execute(toDoData: ToDoData): T {
        val successOrFailure = todoWriter.write(toDoData)

        return successOrFailure.fold(responder::error, responder::success)
    }
}