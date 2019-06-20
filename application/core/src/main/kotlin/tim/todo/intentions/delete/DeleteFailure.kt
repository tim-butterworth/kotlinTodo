package tim.todo.intentions.delete

sealed class DeleteFailure
object ToDoNotFound : DeleteFailure()
object IOError : DeleteFailure()