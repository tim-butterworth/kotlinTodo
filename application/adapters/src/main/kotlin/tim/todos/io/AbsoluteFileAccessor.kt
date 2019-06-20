package tim.todos.io

class AbsoluteFileAccessor(private val filePath: String) : FileAccessor {
    override fun getAbsolutePath(): String {
        return filePath
    }
}