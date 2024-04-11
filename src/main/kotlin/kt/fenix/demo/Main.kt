package kt.fenix.demo

class Main(var id: String, var name: String) {

}

fun main() {

    var main: Main? = Main("12", "tom")

    println(main?.name)
}