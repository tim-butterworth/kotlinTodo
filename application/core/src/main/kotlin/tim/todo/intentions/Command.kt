package tim.todo.intentions

interface Command<T> {
    fun data(): T
}