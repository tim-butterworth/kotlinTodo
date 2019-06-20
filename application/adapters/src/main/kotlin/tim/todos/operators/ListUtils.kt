package tim.todos.operators

import arrow.core.Option

class ListUtils

fun <T> find(list: List<T>, predicate: (T) -> Boolean): Option<T> {
    list.forEach { t ->
        if (predicate(t)) return Option.just(t)
    }
    return Option.empty()
}