package app.kevs.biyang.game

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.TravelumHelper
import app.kevs.biyang.game.libs.adapters.AdapterType
import app.kevs.biyang.game.libs.adapters.MonsterCardAdapter
import app.kevs.biyang.game.libs.data.DataManager
import app.kevs.biyang.game.libs.models.*
import co.metalab.asyncawait.async
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_battle_ground.*
import kotlinx.android.synthetic.main.game_dialog_cards.*
import kotlinx.android.synthetic.main.game_dialog_convert_runes.*
import kotlinx.android.synthetic.main.game_dialog_image_preview.*
import kotlinx.android.synthetic.main.game_dialog_spell_cast.*
import kotlinx.android.synthetic.main.game_dialog_spell_cast.img_preview_container
import kotlinx.android.synthetic.main.game_dialog_spell_cast.lbl_description
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*

class BattleGroundActivity : AppCompatActivity() {
    var API : DataManager? = null
    var PLAYER : PlayerState? = null
    var ENEMY : PlayerState? = null

    var YOUR_TURN : Boolean = true
    set(value) {
        field = value
        if (value){
            card_deck.setColorFilter(Color.argb(255,255,255,255));
            applyTurnEffects(PLAYER!!,ENEMY!!)
        }else{
            card_deck.setColorFilter(Color.argb(255,255,0,0));
            applyTurnEffects(ENEMY!!,PLAYER!!)
        }
    }

    var CARDS : RealmResults<MonsterCard>? = null
    set(value) {
        field = value
        /*if (value != null){
            value.forEach {
                val name = it.name
                val url = it.imgUrl
                async {
                    val bmp = await { Helper.getBitmapFromUrl(url!!) }
                    val imgUrl = Helper.bitmapToFile(name!!, bmp!!)
                }
                api?.transact { it.imgUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images/${name}.png" }
            }
            Helper.toast(this, "Done transfering images")
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_battle_ground)

        API = DataManager(this)

        API?.getCards {
            CARDS = it
        }

        PLAYER = PlayerState(BindView(
            player_stats, player_earth, player_wind, player_water, player_fire, player_light, player_dark
        ))
        PLAYER?.runes?.apply {
            earth = 15; wind = 15; water = 15; fire = 15; light = 15; dark = 15
        }
        PLAYER?.health = MAX_HEALTH

        ENEMY = PlayerState(BindView(
            enemy_stats, enemy_earth, enemy_wind, enemy_water, enemy_fire, enemy_light, enemy_dark
        ))
        ENEMY?.runes?.apply {
            earth = 30; wind = 30; water = 30; fire = 30; light = 30; dark = 30
        }
        ENEMY?.health = MAX_HEALTH

        var ai_id = intent.getStringExtra("ai_id")
        if (!ai_id.isNullOrEmpty()){
            API?.transact {
                AI = it.where(AICharacter::class.java).equalTo("_id",ai_id).findFirst()
            }
        }

        distributeRandomCardsToPlayers(10)

        card_deck.setOnClickListener {
            onDeckPressed()
        }

        your_cards.setOnClickListener {
            if (!YOUR_TURN)
                Helper.toast(this, "Not your turn")
            else
                openYourCards()
        }

        action_back.setOnClickListener { finish() }

        action_convert_runes.setOnClickListener { initConvertRunes(PLAYER!!) }

        action_reset.setOnClickListener {
            Helper.showConfirm(this,"Reset duel?"){

            }
        }
    }

    private fun distributeRandomCardsToPlayers(numberOfCards: Int) {
        var i = 0;
        while (i < numberOfCards){
            val rand1 = (0..CARDS!!.size-1).random()
            val rand2 = (0..CARDS!!.size-1).random()
            PLAYER!!.cards!!.add(CARDS!!.get(rand1)!!)
            ENEMY!!.cards!!.add(CARDS!!.get(rand2)!!)
            i++
        }
    }

    private fun initConvertRunes(player: PlayerState) {
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_convert_runes,0.7){
            it.apply {
                val runes = listOf("Earth","Wind","Water","Fire","Light","Dark")
                spinner_src_rune.adapter = ArrayAdapter(this@BattleGroundActivity,android.R.layout.simple_spinner_dropdown_item,runes)
                spinner_target_rune.adapter = ArrayAdapter(this@BattleGroundActivity,android.R.layout.simple_spinner_dropdown_item,runes)
                txt_runes_max.setText(player.runes.earth.toString())
                seekbar_quantity.max = player.runes.earth
                spinner_src_rune.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        when(position){
                            0 -> {txt_runes_max.setText(player.runes.earth.toString()); seekbar_quantity.max = player.runes.earth}
                            1 -> {txt_runes_max.setText(player.runes.wind.toString()); seekbar_quantity.max = player.runes.wind}
                            2 -> {txt_runes_max.setText(player.runes.water.toString()); seekbar_quantity.max = player.runes.water}
                            3 -> {txt_runes_max.setText(player.runes.fire.toString()); seekbar_quantity.max = player.runes.fire}
                            4 -> {txt_runes_max.setText(player.runes.light.toString()); seekbar_quantity.max = player.runes.light}
                            5 -> {txt_runes_max.setText(player.runes.dark.toString()); seekbar_quantity.max = player.runes.dark}
                        }
                        seekbar_quantity.progress = 0
                        txt_runes_val.setText("0")
                        txt_runes_output.setText("0")
                    }
                }
                seekbar_quantity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        var p = progress / 2 as Int
                        p = p * 2
//                        seekbar_quantity.setProgress(progress)
                        txt_runes_val.setText(p.toString())
                        if (p != 0)
                            txt_runes_output.setText((p/2).toString())
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })

                action_save_changes.setOnClickListener {
                    if (spinner_src_rune.selectedItemPosition == spinner_target_rune.selectedItemPosition){
                        Helper.toast(this@BattleGroundActivity, "Source rune and target rune must not be the same")
                    }else{
                        val p = seekbar_quantity.progress
                        val o = txt_runes_output.text.toString().toInt()
                        when (spinner_src_rune.selectedItemPosition){
                            0 -> player.runes.earth -= p
                            1 -> player.runes.wind -= p
                            2 -> player.runes.water -= p
                            3 -> player.runes.fire -= p
                            4 -> player.runes.light -= p
                            5 -> player.runes.dark -= p
                        }
                        when (spinner_target_rune.selectedItemPosition){
                            0 -> player.runes.earth += o
                            1 -> player.runes.wind += o
                            2 -> player.runes.water += o
                            3 -> player.runes.fire += o
                            4 -> player.runes.light += o
                            5 -> player.runes.dark += o
                        }
                        this.dismiss()
                    }
                }
            }
        }
    }

    private fun applyTurnEffects(who: PlayerState, other: PlayerState) {
        if (who.poisonTurn > 0){
            who.apply { health -= other.spellDamage; poisonTurn--}
        }

        if (who.healTurn > 0){
            who.apply { health += who.spellDamage; healTurn-- }
        }

        if (who.spellBlock > 0){
            who.spellBlock--
        }

        if (who.attackBlock > 0){
            who.attackBlock--
        }


    }

    private fun openYourCards() {
        val spells = SpellBook.getSpells(API!!)
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_cards,0.8){
            it.apply {
                rv_cards.layoutManager = LinearLayoutManager(this@BattleGroundActivity) // GridLayoutManager(this@BattleGroundActivity, 2)
                rv_cards.adapter = MonsterCardAdapter(this@BattleGroundActivity,PLAYER?.cards!!,spells,AdapterType.LIST,
                    onRowClick = {
                        this.dismiss()
                        openCard(it)
                    }){}
            }
        }
    }

    private fun openCard(card: MonsterCard) {
        TravelumHelper.ShowDialog(this,R.layout.getlist_dialog_item_preview,0.7){
            it.apply {
                if (!card.description.isNullOrEmpty()){
                    val allowedLength = if(card.description?.length!! > 600){
                        600
                    }else{
                        card.description?.length
                    }
                    lbl_description.setText(card.description?.substring(0,allowedLength!!))
                }
                val skill = SpellBook.getSpells(API!!).find { it.name.toUpperCase().equals(card.skill!!.toUpperCase()) }
                var required = ""
                if (skill != null){
                    required = "Earth ${skill.runes.earth} / Wind ${skill.runes.wind} / Water ${skill.runes.water} / Fire ${skill.runes.fire} / Light ${skill.runes.light} / Dark ${skill.runes.dark}"
                }
                lbl_description.setText("""
                    ${card.skill ?: "Not Set"}
                    ${skill?.description ?: ""}
                    ${required}
                    ${lbl_description.text}
                """.trimIndent())
                lbl_name.setText(card.name)
                lbl_category.setText(card.category ?: "Not set")
                val url = card.imgUrl

                img_thumb.setBackgroundResource(R.drawable.img3)
                img_thumb.setOnClickListener { openImagePreview(card.imgUrl!!) }
                async {
                    if (!card.imgUrl.isNullOrEmpty()){
                        if(url!!.startsWith("http") && await { Helper.isInternetAvailable() }){
                            async {
                                val bmp = await {Helper.getBitmapFromUrl(url!!)}
                                img_thumb.setImageBitmap(bmp)
                            }
                        }else{
                            img_thumb.setImageBitmap(Helper.decodeBase64ToBitmap(url))
                        }
                    }else{
                        img_thumb.setImageDrawable(resources.getDrawable(R.drawable.img3))
                    }
                }

                action_complete.visibility = View.GONE
                action_update.text = "Tackle"
                action_update.setOnClickListener {
                    PLAYER!!.cards!!.remove(card)
                    ENEMY!!.health -= PLAYER!!.damage - ENEMY!!.armor
                    YOUR_TURN = !YOUR_TURN
                    this.dismiss()
                }

                action_remove.text = "Cast Skill"
                action_remove.setOnClickListener {
                    if (!Spell.isPossible(PLAYER!!,SpellBook.getSpells(API!!).find { it.name.toUpperCase().equals(card.skill!!.toUpperCase()) }!!.runes)){
                        Helper.toast(this@BattleGroundActivity, "Not enough runes")
                    }else{
                        this.dismiss()
                        val castResult = cast(card.skill!!, card.imgUrl!!, PLAYER!!, ENEMY!!)
                        if(!castResult){
                            val msg = if (PLAYER!!.spellBlock > 0) "Can't cast spell" else "Not enough runes"
                            Helper.toast(this@BattleGroundActivity, msg)
                        }else{
                            PLAYER!!.cards!!.remove(card)
                            YOUR_TURN = !YOUR_TURN
                        }
                    }
                }
            }
        }
    }

    private fun openImagePreview(imgUrl: String) {
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_image_preview,0.8){
            val dialog = it
            it.apply {
                async {
                    if (!imgUrl.isNullOrEmpty()){
                        if (imgUrl.startsWith("http") && await { Helper.isInternetAvailable() }){
                            val bmp = await { Helper.getBitmapFromUrl(imgUrl) }
                            img_preview.setImageBitmap(bmp)
                        }else{
                            img_preview.setImageBitmap(Helper.decodeBase64ToBitmap(imgUrl))
                        }
                    }
                }

                img_preview_container.setOnClickListener {
                    dialog.dismiss()
                }

                action_change_thumbnail.visibility = View.GONE
            }

        }
    }

    override fun onPause() {
        super.onPause()
        API?.dispose()
    }

    private fun onDeckPressed() {
        if (hasWinner()){
            return
        }
        if (YOUR_TURN){
            giveRuneToPlayer(PLAYER!!)
        }else{
            aiDecide()
        }
        YOUR_TURN = !YOUR_TURN
    }

    private fun hasWinner(): Boolean {
        if (ENEMY!!.health <= 0){
            Helper.toast(this, "You won!")
            return true
        }else if(PLAYER!!.health <= 0){
            TravelumHelper.ShowDialog(this, R.layout.game_dialog_image_preview, 0.6){
                it.apply {
                    img_preview
                }
            }
            Helper.toast(this, "Enemy was victorios!")
            return true
        }
        return false
    }

    private fun aiDecide() {
        val drawChance = AI?.drawChance?.toInt() ?: 3
        val tackleChance = AI?.tackleChance?.toInt() ?: 5
        val turnType = (0..10).random()
        if (turnType < drawChance) {
            // draw
            giveRuneToPlayer(ENEMY!!)
        } else if (turnType < tackleChance) {
            // tackle
            if (ENEMY?.attackBlock!! > 0){
                aiDecide()
            }else if (ENEMY?.cards!!.size == 0){
                aiDecide()
            }else {
                val randomCardIndex = (0..ENEMY?.cards!!.size - 1).random()
                val card = ENEMY!!.cards!!.get(randomCardIndex)
                ENEMY!!.cards!!.remove(card)
                PLAYER!!.health -= ENEMY!!.damage
                val t = Toast.makeText(this,"Enemy used Tackle",Toast.LENGTH_SHORT)
                t.setGravity(Gravity.TOP,0,0)
                t.show()
            }
        } else {
            // cast
            if (ENEMY?.cards!!.size == 0) {
                aiDecide()
            }else if(ENEMY?.spellBlock!! > 0){
                aiDecide()
            }else{
                val randomCardIndex = (0..ENEMY?.cards!!.size - 1).random()
                val card = ENEMY!!.cards!!.get(randomCardIndex)

                if (!aiDecideCardCast(card)){
                    aiDecide()
                }else if (!Spell.isPossible(ENEMY!!,SpellBook.getSpells(API!!).find { it.name.toUpperCase().equals(card.skill!!.toUpperCase()) }!!.runes)){
                    aiDecide()
                }else{

                    val castResult = cast(card.skill!!, card.imgUrl!!, ENEMY!!, PLAYER!!)
                    if (!castResult) {
                        aiDecide()
                    } else {
                        ENEMY!!.cards!!.remove(card)
                    }
                }

            }
        }
    }

    private fun aiDecideCardCast(card: MonsterCard): Boolean {
        when(card.skill!!.toUpperCase()){
            "REBORN" -> if(ENEMY?.health!! < MAX_HEALTH/2){
                return true
            }else{
                return false
            }
            "ANTIDOTE" -> if(ENEMY?.poisonTurn!! > 0){
                return true
            }else{
                return false
            }
        }
        return true
    }

    private fun giveRuneToPlayer(player : PlayerState) {
        val runeIndex = (0..3).random()
        var msg = ""
        val i = if (AI != null) (AI!!.runeRangeMin..AI!!.runeRangeMax).random() else (1..3).random()
        when (runeIndex) {
            0 -> {
                msg += "Earth +$i"
                player?.runes.earth += i
            }
            1 -> {
                msg += "Wind +$i"
                player?.runes.wind += i
            }
            2 -> {
                msg += "Water +$i"
                player?.runes.water += i
            }
            3 -> {
                msg += "Fire +$i"
                player?.runes.fire += i
            }
        }
        val runeIndex2 = (0..1).random()
        when (runeIndex2) {
            0 -> {
                player?.runes.light += i
                msg += " Light +$i"
            }
            1 -> {
                player?.runes.dark += i
                msg += " Dark +$i"
            }
        }
        val name = if (YOUR_TURN) {
            "You"
        }else{
            "Enemy"
        }
        val cardIndex = (0..CARDS?.size!!-1).random()
        val card = CARDS?.get(cardIndex)
        player.cards!!.add(card!!)
        val gravity = if (YOUR_TURN) Gravity.BOTTOM else Gravity.TOP
        val t = Toast.makeText(this,"$name got $msg\n$name have a new card : ${card?.name} ( ${card?.skill} )",Toast.LENGTH_SHORT)
        t.setGravity(gravity,0,0)
        t.show()
    }

    fun cast(spell : String, cardImageUrl : String, caster : PlayerState, target : PlayerState) : Boolean{
        if (caster.spellBlock > 0){
            return false
        }

        val result = SpellBook.cast(API!!, spell, caster, target)
        if (!result)
            return false

        val spellObject = SpellBook.getSpells(API!!).find { it.name.toUpperCase().equals(spell.toUpperCase()) }

        TravelumHelper.ShowDialog(this,R.layout.game_dialog_spell_cast,0.6){
            Helper.vibratePhone(this@BattleGroundActivity)
            it.apply {
                img_preview_container.setOnClickListener { this.dismiss() }
                img_spell.setOnClickListener { this.dismiss() }
                if (!cardImageUrl.isNullOrEmpty()){
                    if (cardImageUrl.startsWith("http")){
                        async {
                            var bmp = Helper.getBitmapFromUrl(cardImageUrl)
                            img_spell.setImageBitmap(bmp)
                        }
                    }else{
                        img_spell.setImageBitmap(Helper.decodeBase64ToBitmap(cardImageUrl))
                    }
                }
                lbl_message.setText("The caster has cast ${spell.capitalize()}")
                lbl_description.setText(spellObject!!.description)
            }
        }
        // Helper.toast(this, "Caster used $spell")
        return true
    }

    var AI : AICharacter? = null
    set(value) {
        field = value
        ENEMY?.health = value!!.initHealth
        ENEMY?.armor = value!!.initArmor
        ENEMY?.damage = value!!.initDamage
        ENEMY?.spellDamage = value!!.initSpellDamage
    }
}