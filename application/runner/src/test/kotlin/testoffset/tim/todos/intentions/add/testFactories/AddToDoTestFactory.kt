package testoffset.tim.todos.intentions.add.testFactories

import testoffset.tim.todos.intentions.TestFactory
import tim.todo.intentions.add.AddToDo
import tim.todo.intentions.add.AddToDoResponder
import tim.todo.intentions.add.ToDoWriter

class AddToDoTestFactory(
    private val toDoWriterTestFactory: TestFactory<ToDoWriter> = AddToDoWriterTestFactory(),
    private val addToDoResponderTestFactory: TestFactory<AddToDoResponder<Unit>> = AddToDoResponderTestFactory()
) : TestFactory<AddToDo<Unit>> {
    override fun getInstance(): AddToDo<Unit> {
        return AddToDo(
            todoWriter = toDoWriterTestFactory.getInstance(),
            responder = addToDoResponderTestFactory.getInstance()
        )
    }

}
