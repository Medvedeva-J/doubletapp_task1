import kotlin.math.pow
import kotlin.math.sqrt

val pushTypes: Map<String, Push> = mapOf(
    "GenderPush" to GenderPush(),
    "GenderAgePush" to GenderAgePush(),
    "LocationAgePush" to LocationAgePush(),
    "LocationPush" to LocationPush(),
    "TechPush" to TechPush(),
    "AgeSpecificPush" to AgeSpecificPush()
)

fun main() {
    val systemProperties: MutableMap<String, String> = parseData(MySystem.PROPERTIES_COUNT)
    val system: MySystem = MySystem.newInstance(systemProperties)

    val result: MutableList<Push> = mutableListOf()
    for (pushNumber in 1 .. readln().toInt()) {
        val pushProperties: MutableMap<String, String> = parseData(readLine().toString().toInt())

        val nameType = pushProperties["type"]
        val push = pushTypes[nameType]!!.newInstance(pushProperties)

        if (push.filter(system)){
            result.add(push)
            println(push.text)
        }
    }

    if (result.size == 0){
        println(-1)
    }
}

fun parseData(count: Int): MutableMap<String, String> {
    val data: MutableMap<String, String> = mutableMapOf()
    for (i in 1..count){
        val line: List<String> = readLine().toString().split(" ")
        data[line[0]] = line[1]
    }
    return data
}

abstract class Push {
    var text: String? = null
    var type: String? = null

    companion object {
        internal const val AGE = "age"
        internal const val GENDER = "gender"
        internal const val OS_VERSION = "os_version"
        internal const val RADIUS = "radius"
        internal const val X_COORD = "x_coord"
        internal const val Y_COORD = "y_coord"
        internal const val DATE = "expiry_date"
        internal const val TEXT = "text"
        internal const val TYPE = "type"
    }

    abstract fun newInstance(properties: Map<String, String>): Push
    abstract fun filter(system: MySystem): Boolean
}

class LocationPush() : Push() {
    var xCoord: Float? = null
    var yCoord: Float? = null
    var radius: Int? = null
    var date: Long? = null

    override fun newInstance(properties: Map<String, String>): LocationPush {
        val push = LocationPush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.radius = properties[RADIUS]!!.toInt()
        push.date = properties[DATE]!!.toLong()
        push.xCoord = properties[X_COORD]!!.toFloat()
        push.yCoord = properties[Y_COORD]!!.toFloat()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.time <= date!!) && (distanceFilter(system))
    }

    private fun distanceFilter(system: MySystem): Boolean{
        val distance = sqrt((system.xCoord - xCoord!!).pow(2) +
                (system.yCoord - yCoord!!).pow(2))
        return distance <= radius!!
    }
}

class AgeSpecificPush : Push() {
    var age: Int? = null
    var date: Long? = null

    override fun newInstance(properties: Map<String, String>): AgeSpecificPush {
        val push = AgeSpecificPush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.age = properties[AGE]!!.toInt()
        push.date = properties[DATE]!!.toLong()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.time <= date!!) && (system.age >= age!!)
    }
}

class TechPush() : Push() {
    var osVersion: Int? = null

    override fun newInstance(properties: Map<String, String>): TechPush {
        val push = TechPush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.osVersion = properties[OS_VERSION]!!.toInt()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return system.osVersion <= osVersion!!
    }
}

class LocationAgePush() : Push() {
    var xCord: Float? = null
    var yCord: Float? = null
    var radius: Int? = null
    var age: Int? = null

    override fun newInstance(properties: Map<String, String>): LocationAgePush {
        val push = LocationAgePush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.radius = properties[RADIUS]!!.toInt()
        push.age = properties[AGE]!!.toInt()
        push.xCord = properties[X_COORD]!!.toFloat()
        push.yCord = properties[Y_COORD]!!.toFloat()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.age >= age!!) && (distanceFilter(system))
    }

    private fun distanceFilter(system: MySystem): Boolean{
        val xDest: Float = system.xCoord - xCord!!
        val yDest: Float = system.yCoord - yCord!!
        return sqrt(xDest * xDest + yDest * yDest) <= radius!!
    }
}

class GenderPush() : Push(){
    var gender: String? = null

    override fun newInstance(properties: Map<String, String>): GenderPush {
        val push = GenderPush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.gender = properties[GENDER]!!.toString()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return system.gender == gender
    }
}

class GenderAgePush() : Push(){
    var gender: String? = null
    var age: Int? = null

    override fun newInstance(properties: Map<String, String>): GenderAgePush {
        val push = GenderAgePush()
        push.text = properties[TEXT]!!.toString()
        push.type = properties[TYPE]!!.toString()
        push.gender = properties[GENDER]!!.toString()
        push.age = properties[AGE]!!.toInt()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.gender == gender) && (system.age >= age!!)
    }
}

data class MySystem(
    val time: Long,
    val age: Int,
    val gender: String,
    val osVersion: Int,
    val xCoord: Float,
    val yCoord: Float
) {
    companion object {
        const val PROPERTIES_COUNT: Int = 6
        fun newInstance(systemProperties: Map<String, String>): MySystem = MySystem(
            time = systemProperties["time"]!!.toLong(),
            age = systemProperties["age"]!!.toInt(),
            gender = systemProperties["gender"]!!.toString(),
            osVersion = systemProperties["os_version"]!!.toInt(),
            xCoord = systemProperties["x_coord"]!!.toFloat(),
            yCoord = systemProperties["y_coord"]!!.toFloat()
        )
    }
}