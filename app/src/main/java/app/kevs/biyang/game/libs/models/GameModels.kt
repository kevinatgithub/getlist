package app.kevs.biyang.game.libs.models

import android.widget.TextView
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

interface Card{
    var name : String?
    var description : String?
    var imgUrl : String?
    var skill : String?
    var category : String?
}

open class MonsterCard(
    @PrimaryKey
    var _id : String? = null,
    override var name: String? = null,
    override var description: String? = null,
    override var imgUrl: String? = null,
    override var skill: String? = null,
    override var category: String? = null
) : RealmObject(), Card

val MAX_HEALTH = 45

open class BindView(
    var stats : TextView? = null,
    var earth : TextView? = null,
    var wind : TextView? = null,
    var water : TextView? = null,
    var fire : TextView? = null,
    var light : TextView? = null,
    var dark : TextView? = null
)

open class PlayerState(
    var view : BindView? = null,
    _health : Int = MAX_HEALTH,
    _damage : Int = 1,
    _armor : Int = 0,
    _spellDamage : Int = 1,
    var cards : ArrayList<MonsterCard>? = ArrayList<MonsterCard>(),
    _attackBlock : Int = 0,
    _spellBlock : Int = 0,
    _poisonTurn : Int = 0,
    _healTurn : Int = 0,
    var runes : Runes = Runes(0,0,0,0,0,0,view)
){
    var health : Int = _health
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    var damage : Int = _damage
    set(value) {
        field = if (value > 0) value else 0
        view?.stats?.setText(makeStats())
    }

    var armor : Int = _armor
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    var spellDamage : Int = _spellDamage
    set(value) {
        field  = if (value > 0) value else 0
        view?.stats?.setText(makeStats())
    }

    var attackBlock : Int = _attackBlock
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    var spellBlock : Int = _spellBlock
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    var poisonTurn : Int = _poisonTurn
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    var healTurn : Int = _healTurn
    set(value) {
        field = value
        view?.stats?.setText(makeStats())
    }

    private fun makeStats() : String{
        return """
            Health : ${health}
            Damage : ${damage}
            Armor : ${armor}
            Spell Damage : ${spellDamage}
            Attack Block : ${attackBlock}
            Spell Block : ${spellBlock}
            Poisoned Turn : ${poisonTurn}
            Heal Turn : ${healTurn}
        """.trimIndent()
    }
}

class Runes (
    earthD : Int = 0,
    windD : Int = 0,
    waterD : Int = 0,
    fireD : Int = 0,
    lightD : Int = 0,
    darkD : Int = 0,
    var view : BindView? = null
){
    var earth : Int = earthD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.earth?.text = value.toString()
    }

    var wind : Int = windD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.wind?.text = value.toString()
    }

    var water : Int = waterD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.water?.text = value.toString()
    }

    var fire : Int = fireD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.fire?.text = value.toString()
    }

    var light : Int = lightD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.light?.text = value.toString()
    }

    var dark : Int = darkD
    set(value) {
        field = if (value > 0 ) value else 0
        view?.dark?.text = value.toString()
    }
}