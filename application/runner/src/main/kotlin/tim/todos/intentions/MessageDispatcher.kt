package tim.todos.intentions

interface MessageDispatcher<T> {
    operator fun invoke(message: T)
}
