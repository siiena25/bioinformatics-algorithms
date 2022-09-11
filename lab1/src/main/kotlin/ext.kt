import java.io.File

fun File.readFileAsLinesUsingBufferedReader(): List<String> = this.bufferedReader().readLines()
operator fun StringBuilder.plusAssign(string: String) {
    this.append(string)
}