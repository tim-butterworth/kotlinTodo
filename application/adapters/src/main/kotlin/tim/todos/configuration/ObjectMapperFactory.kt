package tim.todos.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun getConfiguredObjectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()

    objectMapper.registerModule(KotlinModule())

    return objectMapper
}