import java.io.File

var writeFile: File = File("result.txt")
var substringLength = 3

fun prepareDataSet(path: String) : ArrayList<Data> {
    val file = File(path)
    val strings = file.readFileAsLinesUsingBufferedReader()
    var initialData: Data? = null
    val data = ArrayList<Data>()
    val additionalInfo: StringBuilder = StringBuilder("")
    val neededData: StringBuilder = StringBuilder("")
    var index = 0
    for (string in strings) {
        if (string.startsWith(">")) {
            if (additionalInfo.isNotEmpty()) {
                if (initialData == null) {
                    initialData = Data(index++, additionalInfo.toString(), neededData.toString())
                    data.add(initialData)
                } else {
                    data.add(Data(index++, additionalInfo.toString(), neededData.toString()))
                }
                additionalInfo.clear()
                neededData.clear()
            }
            additionalInfo += string
        } else {
            neededData += string
        }
    }
    return data
}

fun main(args: Array<String>) {
    val data: ArrayList<Data> = prepareDataSet(args[0])
    calcPercents(data, args[1].toInt())
}

fun calcPercents(data: ArrayList<Data>, indexOfElement: Int) {
    val sb = StringBuilder("Sample: ${data[indexOfElement]}\n")
    val result = arrayListOf<Pair<Data, Double>>()
    for (i in 0 until data.size) {
        val str1 = data[i].stringToCompare
        val str2 = data[indexOfElement].stringToCompare
        val substrings: MutableMap<String, MutableSet<Int>> = getListOfSubstringsFromString(str1)

        var res = 0
        var j = 0
        while (j + substringLength < str2.length) {
            val currentSubstring = str2.substring(j, j + substringLength)
            if (substrings[currentSubstring] != null) {
                ++res
                for(k in -substringLength + 1 until substringLength) {
                    val pos = substrings[currentSubstring]!!.first() + k
                    if (pos >= 0 && pos + substringLength - 1 < str1.length) {
                        val cur2 = str1.substring(pos, pos + substringLength)
                        substrings[cur2]!!.drop(pos)
                    }
                }
                j += substringLength - 1
            }
            ++j
        }
        result.add(Pair(data[i], (2.00 * substringLength * res * 100 / (str1.length + str2.length))))
    }
    result.apply {
        sortByDescending { it.second }
        sb.append(joinToString { elem ->
            "\n\n${elem.first.additionalData}\n${elem.first.stringToCompare}\n${elem.second}%"
        })
    }
    writeFile.appendText(sb.toString(), Charsets.UTF_8)
}

fun getListOfSubstringsFromString(str: String): MutableMap<String, MutableSet<Int>> {
    val substrings: MutableMap<String, MutableSet<Int>> = mutableMapOf()
    var ind2 = substringLength
    for ((ind1, i) in (0..str.length - substringLength).withIndex()) {
        val currentSubstring = str.substring(ind1, ind2)
        if (substrings[currentSubstring] == null) {
            substrings[currentSubstring] = mutableSetOf(i)
        } else {
            val set = substrings[currentSubstring]
            set!!.add(i)
            substrings[currentSubstring] = set
        }
        ++ind2
    }
    return substrings
}