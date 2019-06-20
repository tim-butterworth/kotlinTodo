package tim.todos.commandParsing

import arrow.core.Option

class StringExtractor {

    private var data: Option<StringBuffer> = Option.empty()

    fun retain(char: Char) {
        if (data.isEmpty()) {
            data = Option(StringBuffer())
        }

        data.map { buffer -> buffer.append(char) }
    }

    fun getData(): Option<StringBuffer> = data
}
