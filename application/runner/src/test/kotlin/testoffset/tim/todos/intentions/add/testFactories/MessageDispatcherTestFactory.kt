package testoffset.tim.todos.intentions.add.testFactories

import testoffset.tim.todos.intentions.TestFactory
import tim.todos.intentions.MessageDispatcher
import java.util.*

class MessageDispatcherTestFactory(private val queue: Queue<String> = LinkedList<String>()) :
    TestFactory<MessageDispatcher<String>> {
    override fun getInstance(): MessageDispatcher<String> {
        return object : MessageDispatcher<String> {
            override fun invoke(message: String) {
                queue.add(message)
            }
        }
    }
}
