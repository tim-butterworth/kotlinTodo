package tim.todos

import arrow.core.Either
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import tim.todo.intentions.ReadFailure
import tim.todo.intentions.ReadFailureReason
import tim.todos.intentions.ToDosContainer
import tim.todos.io.AbsoluteFileAccessor
import java.io.File
import java.io.IOException

class ToDosReader(
    private val objectMapper: ObjectMapper,
    private val absoluteFileAccessor: AbsoluteFileAccessor
) {
    fun allTodos(): Either<ReadFailure, ToDosContainer> {
        val todosFile = File(absoluteFileAccessor.getAbsolutePath())

        return try {
            Either.right(objectMapper.readValue(todosFile, ToDosContainer::class.java))
        } catch (e: JsonProcessingException) {
            Either.left(ReadFailure(ReadFailureReason.INVALID_FILE, e.message!!))
        } catch (e: IOException) {
            Either.left(ReadFailure(ReadFailureReason.MISSING_FILE, e.message!!))
        }
    }
}