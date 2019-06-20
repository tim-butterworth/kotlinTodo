package testoffset.tim.todos.intentions.add.testFactories

import testoffset.tim.todos.intentions.TestFactory
import tim.todos.intentions.MessageDispatcher
import tim.todos.intentions.add.AddCommandHandler

class AddCommandHandlerTestFactory(
    private val messageDispatcherFactory: TestFactory<MessageDispatcher<String>> = MessageDispatcherTestFactory(),
    private val addToDoFactory: AddToDoTestFactory = AddToDoTestFactory(),
    private val addCommandParserFactory: AddCommandParserTestFactory = AddCommandParserTestFactory()
) {
    fun getInstance(): AddCommandHandler<Unit> {
        return AddCommandHandler(
            addToDo = addToDoFactory.getInstance(),
            errorQueue = messageDispatcherFactory.getInstance(),
            commandParser = addCommandParserFactory.getInstance()
        )
    }
}