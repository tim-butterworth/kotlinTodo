package tim.todos.intentions

import java.util.concurrent.BlockingQueue

class BlockingMessageDispatcher<T>(private val queue: BlockingQueue<T>) : MessageDispatcher<T> {
    override fun invoke(message: T) {
        queue.put(message)
    }
}