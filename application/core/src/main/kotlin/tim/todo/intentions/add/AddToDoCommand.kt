package tim.todo.intentions.add

import tim.todo.intentions.Command
import tim.todo.intentions.ToDoData

class AddToDoCommand(private val data: ToDoData) : Command<ToDoData> {
    override fun data(): ToDoData = data
}