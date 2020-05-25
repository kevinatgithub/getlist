package app.kevs.biyang.getlist.libs.models

import kotlin.random.Random

object SpellBook{

    //region Heal
    private fun cheer(player: PlayerState){
        player.damage += 2
        player.spellDamage += 1
    }

    private fun rage(player: PlayerState){
        player.armor -= 10
        player.damage += 10
        player.spellDamage += 5
        player.health -= 5
    }

    private fun heal(player: PlayerState, amt : Int){
        player.health = if (player.health + amt > MAX_HEALTH){
            MAX_HEALTH
        }else{
            player.health + amt
        }
    }

    private fun antidote(player: PlayerState){
        player.poisonTurn = 0
    }

    private fun motivate(player: PlayerState){
        player.attackBlock = 0
        player.spellBlock = 0
        player.armor += 2
    }

    private fun purify(player: PlayerState){
        antidote(player)
        motivate(player)
    }

    private fun reborn(player: PlayerState){
        purify(player)
        heal(player, MAX_HEALTH)
    }

    private fun protect(player: PlayerState){
        player.armor += 2
    }

    private fun guard(player: PlayerState, shld : Int){
        player.armor += shld
    }
    //endregion

    //region Damage
    private fun burn(player : PlayerState){
        player.poisonTurn += 5
    }

    private fun freeze(player: PlayerState){
        player.poisonTurn += 2
        player.attackBlock += 2
        player.spellBlock += 2
    }

    private fun shock(player: PlayerState, spellDamage: Int){
        player.attackBlock += 3
        player.health -= spellDamage
    }

    private fun dizzy(player: PlayerState){
        player.damage -= 2
        player.health -= 2
    }

    private fun forget(player: PlayerState){
        player.spellBlock += 2
    }

    private fun silence(player: PlayerState){
        player.spellBlock += 5
    }

    private fun sleep(player: PlayerState){
        player.attackBlock += 5
        player.spellBlock += 5
        player.healTurn += 5
    }

    private fun fear(player: PlayerState){
        player.health -= 5
        player.damage -= 3
        player.armor -= 3
    }

    private fun instantDeath(player: PlayerState){
        val chance = Random.nextDouble()
        if (chance < 0.1){
            player.health = 0
        }
    }
    //endregion

    val spells : List<Pair<String, Runes>> = listOf(
        //region buffs
        Pair("CHEER", Runes(1, 0,0,0,1,0)),
        Pair("RAGE", Runes(1,1,0,0,1,0)),
        Pair("HEAL", Runes(1, 0,0,0,1,0)),
        Pair("ANTIDOTE", Runes(0,2,0,0,1,0)),
        Pair("MOTIVATE", Runes(2,1,0,0,2,0)),
        Pair("PURIFY", Runes(3,2,0,0,2,0)),
        Pair("REBORN", Runes(10,10,10,10,10,0)),
        Pair("PROTECT", Runes(0,0,0,1,1,0)),
        Pair("GUARD", Runes(2,0,1,1,1,0)),
        //endregion
        //region curse
        Pair("BURN", Runes(0,0,0,2,0,1)),
        Pair("FREEZE", Runes(0,0,3,0,0,2)),
        Pair("SHOCK", Runes(0,2,3,0,0,2)),
        Pair("DIZZY", Runes(2,0,0,0,0,2)),
        Pair("FORGET", Runes(1,1,0,0,0,1)),
        Pair("SILENCE", Runes(2,2,0,0,0,2)),
        Pair("SLEEP", Runes(3,3,0,0,0,3)),
        Pair("FEAR", Runes(1,1,1,2,0,1)),
        Pair("INSTANTDEATH", Runes(10,10,10,10,0,10)),
        //endregion
        //region damage
        Pair("BLADEBLAST", Runes(1,1,1,1,0,1)),
        Pair("FLAMEDASH", Runes(0,0,0,4,0,1)),
        Pair("FROSTBOMB", Runes(0,0,4,0,0,1)),
        Pair("SHOCKNOVA", Runes(2,2,0,0,1,0)),
        Pair("SHOCKWAVE", Runes(3,3,0,0,3,0)),
        Pair("SPARK", Runes(2,2,0,0,1,0)),
        Pair("VORTEX", Runes(1,1,1,1,1,1)),
        Pair("STRIKE", Runes(1,1,1,1,1,1)),
        Pair("SLASH", Runes(2,2,2,2,2,2)),
        Pair("BACKSTAB", Runes(3,3,3,3,3,3))
        //endregion
    )

    fun cast(spell : String, player: PlayerState, enemy: PlayerState) : Boolean{
        when(spell.toUpperCase()){
            //region buffs
            "CHEER" -> if(isPossible(player, spells[0].second)){
                cheer(player)
                payRunes(player, spells[0].second)
                return true
            }
            "RAGE" -> if(isPossible(player, spells[1].second)){
                rage(player)
                payRunes(player, spells[1].second)
                return true
            }
            "HEAL" -> if(isPossible(player,spells[2].second)){
                heal(player,10)
                payRunes(player, spells[2].second)
                return true
            }
            "ANTIDOTE" -> if(isPossible(player, spells[3].second)){
                antidote(player)
                payRunes(player, spells[3].second)
                return true
            }
            "MOTIVATE" -> if(isPossible(player, spells[4].second)){
                motivate(player)
                payRunes(player, spells[4].second)
                return true
            }
            "PURIFY" -> if(isPossible(player,spells[5].second)){
                purify(player)
                payRunes(player, spells[5].second)
                return true
            }
            "REBORN" -> if(isPossible(player, spells[6].second)){
                reborn(player)
                payRunes(player, spells[6].second)
                return true
            }
            "PROTECT" -> if(isPossible(player,spells[7].second)){
                protect(player)
                payRunes(player, spells[7].second)
                return true
            }
            "GUARD" -> if(isPossible(player, spells[8].second)){
                guard(player,6)
                payRunes(player, spells[8].second)
                return true
            }
            //endregion
            //region curse
            "BURN" -> if(isPossible(player, spells[9].second)){
                burn(enemy)
                payRunes(player, spells[9].second)
                return true
            }
            "FREEZE" -> if(isPossible(player, spells[10].second)){
                freeze(enemy)
                payRunes(player, spells[10].second)
                return true
            }
            "SHOCK" -> if(isPossible(player, spells[11].second)){
                shock(enemy, player.spellDamage)
                payRunes(player, spells[11].second)
                return true
            }
            "DIZZY" -> if(isPossible(player, spells[12].second)){
                dizzy(enemy)
                payRunes(player, spells[12].second)
                return true
            }
            "FORGET" -> if(isPossible(player, spells[13].second)){
                forget(enemy)
                payRunes(player, spells[13].second)
                return true
            }
            "SILENCE" -> if(isPossible(player, spells[14].second)){
                silence(enemy)
                payRunes(player, spells[14].second)
                return true
            }
            "SLEEP" -> if(isPossible(player, spells[15].second)){
                sleep(enemy)
                payRunes(player, spells[15].second)
                return true
            }
            "FEAR" -> if(isPossible(player, spells[16].second)){
                fear(enemy)
                payRunes(player, spells[16].second)
                return true
            }
            "INSTANTDEATH" -> if(isPossible(player,spells[17].second)){
                instantDeath(enemy)
                payRunes(player, spells[17].second)
                return true
            }
            //endregion
            //region damage
            "BLADEBLAST" -> if(isPossible(player,spells[18].second)){
                enemy.health -= 3
                payRunes(player, spells[18].second)
                return true
            }
            "FLAMEDASH" -> if(isPossible(player,spells[19].second)){
                enemy.health -= 4
                payRunes(player, spells[19].second)
                return true
            }
            "FROSTBOMB" -> if(isPossible(player,spells[20].second)){
                enemy.health -= 4
                payRunes(player, spells[20].second)
                return true
            }
            "SHOCKNOVA" -> if(isPossible(player,spells[21].second)){
                enemy.health -= 5
                payRunes(player, spells[21].second)
                return true
            }
            "SHOCKWAVE" -> if(isPossible(player,spells[22].second)){
                enemy.health -= 8
                payRunes(player, spells[22].second)
                return true
            }
            "SPARK" -> if(isPossible(player,spells[23].second)){
                enemy.health -= 5
                payRunes(player, spells[23].second)
                return true
            }
            "VORTEX" -> if(isPossible(player,spells[24].second)){
                enemy.health -= 10
                payRunes(player, spells[24].second)
                return true
            }
            "STRIKE" -> if(isPossible(player,spells[25].second)){
                enemy.health -= 5
                payRunes(player, spells[25].second)
                return true
            }
            "SLASK" -> if(isPossible(player,spells[26].second)){
                enemy.health -= 10
                payRunes(player, spells[26].second)
                return true
            }
            "BACKSTUB" -> if(isPossible(player,spells[27].second)){
                enemy.health -= 15
                payRunes(player, spells[27].second)
                return true
            }
            //endregion
            else -> return false
        }
        return false
    }

    private fun payRunes(caster: PlayerState, runes: Runes) {
        caster.runes.earth = caster.runes.earth - runes.earth
        caster.runes.wind = caster.runes.wind - runes.wind
        caster.runes.water = caster.runes.water - runes.water
        caster.runes.fire = caster.runes.fire - runes.fire
        caster.runes.light = caster.runes.light - runes.light
        caster.runes.dark = caster.runes.dark - runes.dark
    }

    private fun isPossible(player: PlayerState, runes: Runes): Boolean {
        val canCast = (player.runes.earth > runes.earth &&
                    player.runes.wind > runes.wind &&
                    player.runes.water > runes.water &&
                    player.runes.fire > runes.fire &&
                    player.runes.light > runes.light &&
                    player.runes.dark > runes.dark
                )

        if (canCast){
            player.runes.earth -= runes.earth
            player.runes.wind -= runes.wind
            player.runes.water -= runes.water
            player.runes.fire -= runes.fire
            player.runes.light -= runes.light
            player.runes.dark -= runes.dark
            return true
        }
        return false
    }
}