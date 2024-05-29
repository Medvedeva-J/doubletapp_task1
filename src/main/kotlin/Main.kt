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
    val systemProperties: MutableMap<String, String> = parseData(MySystem.COUNT_PARAM)
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
        internal const val TEXT_FIELD_NAME = "text"
        internal const val TYPE_FIELD_NAME = "type"
        internal const val GENDER_FIELD_NAME = "gender"
        internal const val AGE_FIELD_NAME = "age"
        internal const val RADIUS_FIELD_NAME = "radius"
        internal const val XCORD_FIELD_NAME = "x_coord"
        internal const val YCORD_FIELD_NAME = "y_coord"
        internal const val DATE_FIELD_NAME = "expiry_date"
        internal const val OSVERSION_FIELD_NAME = "os_version"
    }

    abstract fun newInstance(params: Map<String, String>): Push
    abstract fun filter(system: MySystem): Boolean
}

class LocationPush() : Push() {
    var xCord: Float? = null
    var yCord: Float? = null
    var radius: Int? = null
    var date: Long? = null

    override fun newInstance(params: Map<String, String>): LocationPush {
        val push = LocationPush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.radius = params[RADIUS_FIELD_NAME]!!.toInt()
        push.date = params[DATE_FIELD_NAME]!!.toLong()
        push.xCord = params[XCORD_FIELD_NAME]!!.toFloat()
        push.yCord = params[YCORD_FIELD_NAME]!!.toFloat()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.time <= date!!) && (distanceFilter(system))
    }

    private fun distanceFilter(system: MySystem): Boolean{
        val distance = sqrt((system.xCord - xCord!!).pow(2) +
                (system.yCord - yCord!!).pow(2))
        return distance <= radius!!
    }
}

class AgeSpecificPush : Push() {
    var age: Int? = null
    var date: Long? = null

    override fun newInstance(params: Map<String, String>): AgeSpecificPush {
        val push = AgeSpecificPush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.age = params[AGE_FIELD_NAME]!!.toInt()
        push.date = params[DATE_FIELD_NAME]!!.toLong()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.time <= date!!) && (system.age >= age!!)
    }
}

class TechPush() : Push() {
    var osVersion: Int? = null

    override fun newInstance(params: Map<String, String>): TechPush {
        val push = TechPush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.osVersion = params[OSVERSION_FIELD_NAME]!!.toInt()
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

    override fun newInstance(params: Map<String, String>): LocationAgePush {
        val push = LocationAgePush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.radius = params[RADIUS_FIELD_NAME]!!.toInt()
        push.age = params[AGE_FIELD_NAME]!!.toInt()
        push.xCord = params[XCORD_FIELD_NAME]!!.toFloat()
        push.yCord = params[YCORD_FIELD_NAME]!!.toFloat()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return (system.age >= age!!) && (distanceFilter(system))
    }

    private fun distanceFilter(system: MySystem): Boolean{
        val xDest: Float = system.xCord - xCord!!
        val yDest: Float = system.yCord - yCord!!
        return sqrt(xDest * xDest + yDest * yDest) <= radius!!
    }
}

class GenderPush() : Push(){
    var gender: String? = null

    override fun newInstance(params: Map<String, String>): GenderPush {
        val push = GenderPush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.gender = params[GENDER_FIELD_NAME]!!.toString()
        return push
    }

    override fun filter(system: MySystem): Boolean {
        return system.gender == gender
    }
}

class GenderAgePush() : Push(){
    var gender: String? = null
    var age: Int? = null

    override fun newInstance(params: Map<String, String>): GenderAgePush {
        val push = GenderAgePush()
        push.text = params[TEXT_FIELD_NAME]!!.toString()
        push.type = params[TYPE_FIELD_NAME]!!.toString()
        push.gender = params[GENDER_FIELD_NAME]!!.toString()
        push.age = params[AGE_FIELD_NAME]!!.toInt()
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
    val xCord: Float,
    val yCord: Float
) {
    companion object {
        const val COUNT_PARAM: Int = 6
        fun newInstance(systemParams: Map<String, String>): MySystem = MySystem(
            time = systemParams["time"]!!.toLong(),
            age = systemParams["age"]!!.toInt(),
            gender = systemParams["gender"]!!.toString(),
            osVersion = systemParams["os_version"]!!.toInt(),
            xCord = systemParams["x_coord"]!!.toFloat(),
            yCord = systemParams["y_coord"]!!.toFloat()
        )
    }
}