package Persistence

interface Serializer {
    @Throws(Exception::class)
    fun write(obj: Any?)

    @Throws(Exception::class)
    fun read(): Any?
}