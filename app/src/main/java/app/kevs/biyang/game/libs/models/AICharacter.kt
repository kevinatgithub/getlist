package app.kevs.biyang.game.libs.models

import io.realm.RealmObject
import java.util.*

open class AICharacter(
    var _id : String = UUID.randomUUID().toString(),
    var name : String? = null,
    var description : String? = null,
    var imgUrl : String? = null,
    var initHealth : Int = 0,
    var initDamage : Int = 0,
    var initSpellDamage : Int = 0,
    var initArmor : Int = 0,
    var drawChance : Float = 2.0f,
    var tackleChance : Float = 4.0f,
    var earth : Int = 15,
    var wind : Int = 15,
    var water : Int = 15,
    var fire : Int = 15,
    var light : Int = 15,
    var dark : Int = 15,
    var runeRangeMin : Int = 3,
    var runeRangeMax : Int = 5
):RealmObject()