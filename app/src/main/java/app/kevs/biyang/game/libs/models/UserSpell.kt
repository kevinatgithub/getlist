package app.kevs.biyang.game.libs.models

import io.realm.RealmObject
import java.util.*

open class UserSpell(
    var _id : String = UUID.randomUUID().toString(),
    var name : String? = null,
    var description : String? = null,
    var earth : Int = 0,
    var wind : Int = 0,
    var water : Int = 0,
    var fire : Int = 0,
    var light : Int = 0,
    var dark : Int = 0,
    var jsonEffect : String? = null
) : RealmObject()