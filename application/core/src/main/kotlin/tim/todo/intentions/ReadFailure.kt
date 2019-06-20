package tim.todo.intentions

data class ReadFailure(val readFailureReason: ReadFailureReason, val message: String)