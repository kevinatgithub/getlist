package app.kevs.biyang.getlist

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.getlist.libs.Helper
import app.kevs.biyang.getlist.libs.TravelumHelper
import app.kevs.biyang.getlist.libs.adapters.AdapterType
import app.kevs.biyang.getlist.libs.adapters.MonsterCardAdapter
import app.kevs.biyang.getlist.libs.data.DataManager
import app.kevs.biyang.getlist.libs.models.*
import co.metalab.asyncawait.async
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_battle_ground.*
import kotlinx.android.synthetic.main.game_dialog_cards.*
import kotlinx.android.synthetic.main.game_dialog_image_preview.*
import kotlinx.android.synthetic.main.game_dialog_spell_cast.*
import kotlinx.android.synthetic.main.game_dialog_spell_cast.img_preview_container
import kotlinx.android.synthetic.main.game_dialog_spell_cast.lbl_description
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*

class BattleGroundActivity : AppCompatActivity() {
    var player : PlayerState? = null
    var enemy : PlayerState? = null
    var yourTurn : Boolean = true
    set(value) {
        field = value
        if (value){
            card_deck.setColorFilter(Color.argb(255,255,255,255));
            applyTurnEffects(player!!,enemy!!)
        }else{
            card_deck.setColorFilter(Color.argb(255,255,0,0));
            applyTurnEffects(enemy!!,player!!)
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

    var api : DataManager? = null
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

        api = DataManager(this)

        api?.getCards {
            CARDS = it
        }

        player = PlayerState(BindView(
            player_stats, player_earth, player_wind, player_water, player_fire, player_light, player_dark
        ))
        player?.runes?.apply {
            earth = 15; wind = 15; water = 15; fire = 15; light = 15; dark = 15
        }
        player?.health = MAX_HEALTH

        enemy = PlayerState(BindView(
            enemy_stats, enemy_earth, enemy_wind, enemy_water, enemy_fire, enemy_light, enemy_dark
        ))
        enemy?.runes?.apply {
            earth = 15; wind = 15; water = 15; fire = 15; light = 15; dark = 15
        }
        enemy?.health = MAX_HEALTH

        card_deck.setOnClickListener {
            onDeckPressed()
        }

        your_cards.setOnClickListener {
            openYourCards()
        }

        action_back.setOnClickListener { finish() }
    }

    private fun openYourCards() {
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_cards,0.8){
            it.apply {
                rv_cards.layoutManager = LinearLayoutManager(this@BattleGroundActivity) // GridLayoutManager(this@BattleGroundActivity, 2)
                rv_cards.adapter = MonsterCardAdapter(this@BattleGroundActivity,CARDS!!,AdapterType.LIST,
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
                val skill = SpellBook.spells.find { it.first.equals(card.skill) }
                var required = ""
                if (skill != null){
                    required = "Earth ${skill.second.earth} / Wind ${skill.second.wind} / Water ${skill.second.water} / Fire ${skill.second.fire} / Light ${skill.second.light} / Dark ${skill.second.dark}"
                }
                lbl_description.setText("${card.skill ?: "Not Set"}\n(${required})\n${lbl_description.text}")
                lbl_name.setText(card.name)
                lbl_category.setText(card.category ?: "Not set")
                val url = card.imgUrl

                img_thumb.setBackgroundResource(R.drawable.img3)
                img_thumb.setOnClickListener { openImagePreview(card.imgUrl!!) }
                async {
                    if (!card.imgUrl.isNullOrEmpty() && await { Helper.isInternetAvailable() }){
                        val url = card.imgUrl
                        async {
                            val bmp = await {Helper.getBitmapFromUrl(url!!)}
                            img_thumb.setImageBitmap(bmp)
                        }
                    }else{
                        img_thumb.setImageDrawable(resources.getDrawable(R.drawable.img3))
                    }
                }

                action_complete.visibility = View.GONE
                action_update.text = "Tackle"
                action_remove.text = "Cast Skill"
                action_remove.setOnClickListener {
                    this.dismiss()
                    val castResult = cast(card.skill!!, player!!, enemy!!)
                    if(!castResult){
                        val msg = if (player!!.spellBlock > 0) "Can't cast spell" else "Not enough runes"
                        Helper.toast(this@BattleGroundActivity, msg)
                    }else{
                        yourTurn = !yourTurn
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
                    val bmp = await { Helper.getBitmapFromUrl(imgUrl) }
                    img_preview.setImageBitmap(bmp)
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
        api?.dispose()
    }

    private fun onDeckPressed() {
        if (yourTurn){
            giveRuneToPlayer(player!!)
        }else{
            giveRuneToPlayer(enemy!!)
        }
        yourTurn = !yourTurn
    }

    private fun giveRuneToPlayer(player : PlayerState) {
        val runeIndex = (0..3).random()
        var msg = ""
        when (runeIndex) {
            0 -> {
                msg += "Earth +1"
                player?.runes.earth += 1
            }
            1 -> {
                msg += "Wind +1"
                player?.runes.wind += 1
            }
            2 -> {
                msg += "Water +1"
                player?.runes.water += 1
            }
            3 -> {
                msg += "Fire +1"
                player?.runes.fire += 1
            }
        }
        val runeIndex2 = (0..1).random()
        when (runeIndex2) {
            0 -> {
                player?.runes.light += 1
                msg += " Light +1"
            }
            1 -> {
                player?.runes.dark += 1
                msg += " Dark +1"
            }
        }
        val name = if (yourTurn) {
            "You"
        }else{
            "Enemy"
        }
        Helper.toast(this,"$name got $msg")
    }

    fun cast(spell : String,caster : PlayerState, target : PlayerState) : Boolean{
        if (caster.spellBlock > 0){
            return false
        }

        val result = SpellBook.cast(spell, caster, target)
        if (!result)
            return false

        TravelumHelper.ShowDialog(this,R.layout.game_dialog_spell_cast,0.6){
            it.apply {
                async {
                    if (Helper.isNetworkConnected(this@BattleGroundActivity) && await { Helper.isInternetAvailable() } ){
                        val urls = await { Helper.getRelatedImagesUrlFromWeb(spell,1) }
                        val bmp = await { Helper.getBitmapFromUrl(urls[0]) }
                        img_spell.setImageBitmap(bmp)
                        val description = await { Helper.getDescriptionFromWikipedia(spell) }
                        lbl_description.setText(description)
                    }
                }
                lbl_message.setText("The caster has cast ${spell.capitalize()}")
            }
        }
        return true
    }
}