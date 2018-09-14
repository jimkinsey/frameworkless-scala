package todo

object Greet
{
    def apply(name: String): String = {
        s"Hello, $name!"
    }
}

class GreetTests
{
    def testApply(): Unit = {
        assert(Greet.apply("Jim") == "Hello, Jim!")
    }
}