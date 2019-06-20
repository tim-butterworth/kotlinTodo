package tim.todos.runner.router

import tim.todo.intentions.add.AddToDo
import tim.todo.intentions.delete.DeleteToDo
import tim.todo.intentions.readAll.AllToDos
import tim.todos.ErrorResponder
import tim.todos.intentions.BlockingMessageDispatcher
import tim.todos.intentions.MissingCommandHandler
import tim.todos.intentions.add.AddCommandHandler
import tim.todos.intentions.add.AddToDoResponderCommandLine
import tim.todos.intentions.add.AddToDoWriterFactory
import tim.todos.intentions.add.parseAddCommand
import tim.todos.intentions.delete.DeleteToDoCommandHandler
import tim.todos.intentions.delete.DeleteToDoResponderCommandLine
import tim.todos.intentions.delete.ToDoDeleterFactory
import tim.todos.intentions.delete.parseDeleteCommand
import tim.todos.intentions.readAll.AllToDoCommandHandler
import tim.todos.intentions.readAll.AllToDoProviderFactory
import tim.todos.intentions.readAll.AllToDoResponderCommandLine
import java.util.concurrent.BlockingQueue

class CommandRouterFactory {

    fun getInstance(
        outQueue: BlockingQueue<String>,
        errorQueue: BlockingQueue<String>
    ): CommandRouter {
        val absoluteFilePath = "/Users/swathimakkena/workspace/stufftodo/file.json"
        val allToDosProvider = AllToDoProviderFactory().withAbsoluteFileAccessor(absoluteFilePath)
        val addToDoWriter = AddToDoWriterFactory().withAbsoluteFileAccessor(absoluteFilePath)
        val todoDeleter = ToDoDeleterFactory().getInstance(absoluteFilePath)

        val errorDispatcher = BlockingMessageDispatcher(errorQueue)

        val allToDosResponder = AllToDoResponderCommandLine(BlockingMessageDispatcher(outQueue), errorDispatcher)
        val addToDoResponder = AddToDoResponderCommandLine(BlockingMessageDispatcher(outQueue), errorDispatcher)
        val deleteToDoResponder = DeleteToDoResponderCommandLine(
            BlockingMessageDispatcher(outQueue),
            errorDispatcher
        )

        val addToDo = AddToDo(
            todoWriter = addToDoWriter,
            responder = addToDoResponder
        )
        val allToDos = AllToDos(
            provider = allToDosProvider,
            responder = allToDosResponder
        )
        val deleteToDo = DeleteToDo(
            deleter = todoDeleter,
            responder = deleteToDoResponder
        )

        return CommandRouter(
            listOf(
                AddCommandHandler(addToDo, errorDispatcher) { command -> parseAddCommand(command) },
                AllToDoCommandHandler(allToDos, errorDispatcher),
                DeleteToDoCommandHandler(deleteToDo, errorDispatcher) { command -> parseDeleteCommand(command) },
                MissingCommandHandler(ErrorResponder(errorDispatcher))
            )
        )
    }
}
