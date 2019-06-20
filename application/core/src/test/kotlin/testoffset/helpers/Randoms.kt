package testoffset.helpers

import java.util.*

fun getRandomBetween(minimum: Int, maximum: Int): Int {
    val random = Random()
    return random.nextInt((maximum - minimum) + 1) + minimum
}