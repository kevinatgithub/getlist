package app.kevs.biyang.game.libs.models

import app.kevs.biyang.game.libs.data.DataManager
import kotlin.random.Random

class Spell(var name : String,
            var description : String,
            var runes : Runes,
            var invoke : (player : PlayerState, enemy : PlayerState) -> Unit){

    companion object {
        fun isPossible(player: PlayerState, runes: Runes): Boolean {
            val canCast = (player.runes.earth >= runes.earth &&
                    player.runes.wind >= runes.wind &&
                    player.runes.water >= runes.water &&
                    player.runes.fire >= runes.fire &&
                    player.runes.light >= runes.light &&
                    player.runes.dark >= runes.dark
                    )

            if (canCast){
                return true
            }
            return false
        }

        fun payRunes(caster: PlayerState, runes: Runes) {
            caster.runes.earth = caster.runes.earth - runes.earth
            caster.runes.wind = caster.runes.wind - runes.wind
            caster.runes.water = caster.runes.water - runes.water
            caster.runes.fire = caster.runes.fire - runes.fire
            caster.runes.light = caster.runes.light - runes.light
            caster.runes.dark = caster.runes.dark - runes.dark
        }
    }

    fun cast(player: PlayerState, enemy: PlayerState) : Boolean{

        invoke(player, enemy)
        payRunes(player, runes)
        return true
//        if(isPossible(player, runes)){
//        }
//        return false
    }


}

object SpellBook{

    val spells : List<Spell> = listOf(
        //region Buffs
        Spell("CHEER",
            "Damage + 2. Spell Damage + 2",
            Runes(1,0,0,0,1,0)){
            player, enemy ->
            player.damage += 2
            player.spellDamage += 2
        },
        Spell("RAGE",
            "Damge + 10, Spell Damage + 5, Health - 5",
            Runes(1,1,0,0,1,0)){
            player, enemy ->
            player.armor -= 10
            player.damage += 10
            player.spellDamage += 5
            player.health -= 5
        },
        Spell("HEAL",
            "Health + SD",
            Runes(1,0,0,0,1,0)){
            player, enemy ->
            player.health = if (player.health + player.spellDamage > MAX_HEALTH){
                MAX_HEALTH
            }else{
                player.health + player.spellDamage
            }
        },
        Spell("ANTIDOTE",
            "Clears Poisoned Turn",
            Runes(0,2,0,0,1,0)){
            player, enemy -> player.poisonTurn = 0
        },
        Spell("MOTIVATE",
            "Attack Block = 0, Spell Block = 0, Armor + 2",
            Runes(2,1,0,0,2,0)){
            player, enemy ->
            player.attackBlock = 0
            player.spellBlock = 0
            player.armor += 2
        },
        Spell("PURIFY",
            "Clears Poison, Attack Block = 0, Spell Block = 0, Armor + 2",
            Runes(3,2,0,0,2,0)){
            player, enemy ->
            player.poisonTurn = 0
            player.attackBlock = 0
            player.spellBlock = 0
            player.armor += 2
        },
        Spell("REBORN",
            "Health = ${MAX_HEALTH}, Armor + 2, Clears Poison, Attack Block = 0, Spell Block = 0",
            Runes(10,10,10,10,10,0)){
            player, enemy ->
            player.poisonTurn = 0
            player.attackBlock = 0
            player.spellBlock = 0
            player.armor += 2
            player.health = MAX_HEALTH
        },
        Spell("PROTECT",
            "Armor + 2",
            Runes(0,0,0,1,1,0)){
            player, enemy -> player.armor += 2
        },
        Spell("GUARD",
            "Armor + 6",
            Runes(2,0,1,1,1,0)){
            player, enemy -> player.armor += 6
        },
        Spell("CALIBRATE",
            "Damage + 2",
            Runes(1,0,1,0,1,0)){
            player, enemy -> player.damage += 2
        },
        Spell("FOCUS",
            "Damage + 5",
            Runes(5,5,5,5,5,5)){
            player, enemy -> player.damage += 5
        },
        Spell("MASTERY",
            "70% Chance : Damage + 10",
            Runes(10,10,10,10,10,10)){
            player, enemy ->
            val rand = (0..10).random()
            if (rand < 7)
                player.damage += 10
        },
        Spell("ENCHANT",
            "Spell Damage + 2",
            Runes(1,1,1,1,0,1)){
            player, enemy -> player.spellDamage += 2
        },
        Spell("MEDITATE",
            "Spell Damage + 5",
            Runes(5,5,5,5,5,5)){
            player, enemy -> player.spellDamage += 5
        },
        Spell("BLESSING",
            "70% Chance : Spell Damage + 10",
            Runes( 10,10,10,10,10,10)){
            player, enemy ->
            val rand = (0..10).random()
            if (rand < 7)
                player.spellDamage += 10
        },
        //endregion
        //region Curse
        Spell("CLUTTER",
            "Damage - 2",
            Runes(1,0,1,0,1,0)){
                player, enemy -> enemy.damage -= 2
        },
        Spell("DESTRUCT",
            "Damage - 5",
            Runes(5,5,5,5,5,5)){
                player, enemy -> enemy.damage -= 5
        },
        Spell("NOVICE",
            "70% chance : Damage - 10",
            Runes(10,10,10,10,10, 10)){
                player, enemy ->
                val rand = (0..10).random()
                if (rand < 7)
                    enemy.damage -= 10
        },
        Spell("REPEL",
            "Spell Damage - 2",
            Runes(1,1,1,1,0,1)){
                player, enemy -> enemy.spellDamage -= 2
        },
        Spell("CONSUME",
            "Spell Damage - 5",
            Runes(5,5,5,5,5,5)){
                player, enemy -> enemy.spellDamage -= 5
        },
        Spell("CONDEMN",
            "70% Chance : Spell Damage - 10",
            Runes(10,10,10,10,10,10)){
                player, enemy ->
                val rand = (0..10).random()
                if (rand < 7)
                    enemy.spellDamage -= 10
        },
        Spell("BURN",
            "Poison Turn + 5",
            Runes(0,0,0,2,0,1)){
            player, enemy -> enemy.poisonTurn += 5
        },
        Spell("FREEZE",
            "Poison Turn + 2, Attack Block + 2, Spell Block + 2",
            Runes(0,0,3,0,0,2)){
            player, enemy ->
            enemy.poisonTurn += 2
            enemy.attackBlock += 2
            enemy.spellBlock += 2
        },
        Spell("SHOCK",
            "Attack Block + 3, Health - SD",
            Runes(0,2,3,0,0,2)){
            player, enemy ->
            enemy.attackBlock += 3
            enemy.health -= player.spellDamage - enemy.armor
        },
        Spell("DIZZY",
            "Damage - 2, Health - SD",
            Runes(2,0,0,0,0,2)){
            player, enemy ->
            enemy.damage -= 2
            enemy.health -= player.spellDamage - enemy.armor
        },
        Spell("FORGET",
            "Spell Block + 2",
            Runes(1,1,0,0,0,1)){
            player, enemy -> enemy.spellBlock += 2
        },
        Spell("SILENCE",
            "Spell Block + 5",
            Runes(2,2,0,0,0,2)){
            player, enemy -> enemy.spellBlock += 5
        },
        Spell("SLEEP",
            "Attack Block + 5, Spell Block + 5, Heal Turn  + 5",
            Runes(3,3,0,0,0,3)){
            player, enemy ->
            enemy.attackBlock += 5
            enemy.spellBlock += 5
            enemy.healTurn += 5
        },
        Spell("FEAR",
            "Health - SD, Damage - 2, Spell Damage -2, Armor - 2",
            Runes(1,1,1,2,0,1)){
            player, enemy ->
            enemy.health -= player.spellDamage - enemy.armor
            enemy.damage -= 3
            enemy.armor -= 3
        },
        Spell("INSTANTDEATH",
            "35% chance : health = 0",
            Runes(10,10,10,10,0,10)){
            player, enemy ->
            val chance = Random.nextDouble()
            val healthDeff : Double = if (enemy.health > player.health){
                ((enemy.health - player.health) /100).toDouble()
            }else{
                0.0
            }
            val chancePercentage = 0.35 - healthDeff
            if (chance < chancePercentage){
                enemy.health = 0
            }
        },
        //endregion
        //region Damage
        Spell("BLADEBLAST",
            "Health - 3",
            Runes(1,1,1,1,0,1)){
            player, enemy -> enemy.health -= 3
        },
        Spell("FLAMEDASH",
            "Health - 5",
            Runes(0,0,0,4,0,1)){
            player, enemy -> enemy.health -= 5
        },
        Spell("FROSTBOMB",
            "Health - 5",
            Runes(0,0,4,0,0,1)){
            player, enemy -> enemy.health -= 5
        },
        Spell("SHOCKNOVA",
            "Health - 7",
            Runes(2,2,0,0,1,0)){
            player, enemy -> enemy.health -= 7
        },
        Spell("SHOCKWAVE",
            "Health - 8",
            Runes(3,3,0,0,3,0)){
            player, enemy -> enemy.health -= 8
        },
        Spell("SPARK",
            "Health - 5",
            Runes(2,2,0,0,1,0)){
            player, enemy -> enemy.health -= 5
        },
        Spell("VORTEX",
            "Health - 10",
            Runes(1,1,1,1,1,1)){
            player, enemy -> enemy.health -= 10
        },
        Spell("STRIKE",
            "Health - 5",
            Runes(1,1,1,1,1,1)){
            player, enemy -> enemy.health -= 5
        },
        Spell("SLASH",
            "Health - 10",
            Runes(2,2,2,2,2,2)){
            player, enemy -> enemy.health -= 10
        },
        Spell("BACKSTAB",
            "Health - 15",
            Runes(3,3,3,3,3,3)){
            player, enemy -> enemy.health -= 15
        }
        //endregion
    )

    fun getSpells(api : DataManager) : List<Spell>{
        val spells = ArrayList<Spell>()
        spells.addAll(this.spells)

        api.transact {
            val userSpells = it.where(UserSpell::class.java).findAll()
            userSpells.forEach {
                spells.add(Spell(
                    it.name!!,
                    it.description!!,
                    Runes(it.earth,it.wind,it.water,it.fire,it.light,it.dark)
                ){
                    player, enemy ->
                    val effects =it.jsonEffect!!.split("\n")
                    effects.forEach {
                        val p = it.toString().split(" ")
                        val target = when(p[0].toUpperCase()){
                            "PLAYER" -> player
                            "ENEMY" -> enemy
                            else -> player
                        }

                        var prop = when(p[1].toUpperCase()){
                            "HEALTH" -> target.health
                            "ARMOR" -> target.armor
                            "DAMAGE" -> target.damage
                            "SPELLDAMAGE" -> target.spellDamage
                            "ATTACKBLOCK" -> target.attackBlock
                            "SPELLBLOCK" -> target.spellBlock
                            "POISONTURN" -> target.poisonTurn
                            "HEALTURN" -> target.healTurn
                            else -> target.health
                        }

                        when(p[2]){
                            "+" ->
                                when(p[1].toUpperCase()){
                                    "HEALTH" -> target.health += p[3].toInt()
                                    "ARMOR" -> target.armor += p[3].toInt()
                                    "DAMAGE" -> target.damage += p[3].toInt()
                                    "SPELLDAMAGE" -> target.spellDamage += p[3].toInt()
                                    "ATTACKBLOCK" -> target.attackBlock += p[3].toInt()
                                    "SPELLBLOCK" -> target.spellBlock += p[3].toInt()
                                    "POISONTURN" -> target.poisonTurn += p[3].toInt()
                                    "HEALTURN" -> target.healTurn += p[3].toInt()
                                    else -> target.health += p[3].toInt()
                                }
                            "-" ->
                                when(p[1].toUpperCase()){
                                    "HEALTH" -> target.health -= p[3].toInt()
                                    "ARMOR" -> target.armor -= p[3].toInt()
                                    "DAMAGE" -> target.damage -= p[3].toInt()
                                    "SPELLDAMAGE" -> target.spellDamage -= p[3].toInt()
                                    "ATTACKBLOCK" -> target.attackBlock -= p[3].toInt()
                                    "SPELLBLOCK" -> target.spellBlock -= p[3].toInt()
                                    "POISONTURN" -> target.poisonTurn -= p[3].toInt()
                                    "HEALTURN" -> target.healTurn -= p[3].toInt()
                                    else -> target.health -= p[3].toInt()
                                }
                            "=" ->
                                when(p[1].toUpperCase()){
                                    "HEALTH" -> target.health = p[3].toInt()
                                    "ARMOR" -> target.armor = p[3].toInt()
                                    "DAMAGE" -> target.damage = p[3].toInt()
                                    "SPELLDAMAGE" -> target.spellDamage = p[3].toInt()
                                    "ATTACKBLOCK" -> target.attackBlock = p[3].toInt()
                                    "SPELLBLOCK" -> target.spellBlock = p[3].toInt()
                                    "POISONTURN" -> target.poisonTurn = p[3].toInt()
                                    "HEALTURN" -> target.healTurn = p[3].toInt()
                                    else -> target.health = p[3].toInt()
                                }
                        }
                    }
            }) }
        }

        return spells
    }

    fun cast(api : DataManager, spell : String, player: PlayerState, enemy: PlayerState) : Boolean{
        getSpells(api).forEach {
            if (it.name.toUpperCase().equals(spell.toUpperCase())){
                it.cast(player, enemy)
                return true
            }
        }
        return false
    }
}