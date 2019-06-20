package tim.todo.intentions.readAll

class AllToDos<T>(private val provider: AllToDoProvider, private val responder: AllToDoResponder<T>) {
    fun execute(): T {
        val errorOrTodos = provider.getAll()

        return errorOrTodos.fold(responder::error, responder::success)
    }
}