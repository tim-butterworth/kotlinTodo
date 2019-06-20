package tim.todos.runner

import tim.todos.runner.machine.SystemStreamsProvider
import tim.todos.runner.machine.ToDoMachineFactory
import java.util.*

class Runner

fun main() {
    val properties = Properties()
    properties.load(Runner::class.java.classLoader.getResourceAsStream("application.properties"))

    println("Hello!")

//    listOf("one", "two", "three", "four").forEach {
//        println(it)
//    }
    ToDoMachineFactory().getInstance(SystemStreamsProvider()).startMachine()
}
