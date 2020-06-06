package app.kevs.biyang.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.ItemImage
import app.kevs.biyang.game.libs.ItemImageAdapter
import app.kevs.biyang.game.libs.TravelumHelper
import app.kevs.biyang.game.libs.data.DataManager
import app.kevs.biyang.game.libs.models.AICharacter
import co.metalab.asyncawait.async
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_aicharacters.*
import kotlinx.android.synthetic.main.game_dialog_ai_form.*
import kotlinx.android.synthetic.main.getlist_row_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class AICharacterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_aicharacters)
        API = DataManager(this)

        getRecords()

        itemsswipetorefresh.setOnRefreshListener {
            getRecords()
        }

        action_create.setOnClickListener {
            showAiForm()
        }

        action_cards.setOnClickListener { startActivity(Intent(this, MonsterCardsActivity::class.java)) }

        action_spells.setOnClickListener { startActivity(Intent(this, UserSpellActivity::class.java)) }
    }

    private fun showAiForm(ai : AICharacter? = null) {
        TravelumHelper.ShowDialog(this, R.layout.game_dialog_ai_form, 0.8){
            class OnlyHere{
                var IMAGES = ArrayList<ItemImage>()
                set(value) {
                    field = value
                    if (value != null){
                        it.rc_imgs.layoutManager = LinearLayoutManager(this@AICharacterActivity, LinearLayoutManager.HORIZONTAL, false)
                        it.rc_imgs.adapter = ItemImageAdapter(this@AICharacterActivity, value){
                            val item = IMAGES.get(it)

                            val images = ArrayList<ItemImage>()
                            IMAGES.map {
                                it.isActive = false
                                images.add(it)
                            }
                            IMAGES = images

                            item.isActive = true
                            IMAGES.removeAt(it)
                            IMAGES.add(0, item)
                        }
                        it.rc_imgs.visibility = View.VISIBLE
                    }
                }

                fun apply(){
                    it.apply {
                        if (ai != null){
                            txt_ai_name.setText(ai.name)
                            txt_ai_description.setText(ai.description)
                            txt_ai_health.setText(ai.initHealth.toString())
                            txt_ai_armor.setText(ai.initArmor.toString())
                            txt_ai_damage.setText(ai.initDamage.toString())
                            txt_ai_spell_damage.setText(ai.initSpellDamage.toString())
                            txt_ai_draw.setText(ai.drawChance.toString())
                            txt_ai_tackle.setText(ai.tackleChance.toString())
                            txt_ai_earth.setText(ai.earth.toString())
                            txt_ai_wind.setText(ai.wind.toString())
                            txt_ai_water.setText(ai.water.toString())
                            txt_ai_fire.setText(ai.fire.toString())
                            txt_ai_light.setText(ai.light.toString())
                            txt_ai_dark.setText(ai.dark.toString())
                            txt_ai_runes_min.setText(ai.runeRangeMin.toString())
                            txt_ai_runes_max.setText(ai.runeRangeMax.toString())
                            action_save_changes.text = "Save Changes"
                            val name = ai.name!!
                            val imgUrl = ai.imgUrl
                            async {
                                if (await { Helper.isInternetAvailable() }){
                                    val imgs = await { Helper.getRelatedImagesUrlFromWeb(name) }
                                    val imgsList = ArrayList<ItemImage>()
                                    if (!imgUrl.isNullOrEmpty()){
                                        if (imgUrl.startsWith("http")){
                                            if (await { Helper.isInternetAvailable() }){
                                                val bmp = await { Helper.getBitmapFromUrl(imgUrl) }
                                                imgsList.add(ItemImage(imgUrl!!, bmp!!, true))
                                            }
                                        }else{
                                            var bmp = Helper.decodeBase64ToBitmap(ai.imgUrl!!)
                                            imgsList.add(ItemImage(ai.imgUrl!!, bmp!!, true))
                                        }
                                    }
                                    imgs.forEach {
                                        val bmp = await { Helper.getBitmapFromUrl(it) }
                                        imgsList.add(ItemImage(it, bmp!!, false))
                                    }
                                    IMAGES = imgsList
                                }
                            }

                            action_delete_ai.visibility = View.VISIBLE
                            action_delete_ai.setOnClickListener {
                                this.dismiss()
                                initDeleteAi(ai)
                            }
                        }else{
                            action_delete_ai.visibility = View.INVISIBLE
                        }

                        txt_ai_name.setOnFocusChangeListener { v, hasFocus ->
                            if (!hasFocus){
                                async {
                                    val imgs = await { Helper.getRelatedImagesUrlFromWeb(txt_ai_name.text.toString()!!) }
                                    val imgsList = ArrayList<ItemImage>()
                                    imgs.forEach {
                                        val bmp = await { Helper.getBitmapFromUrl(it) }
                                        imgsList.add(ItemImage(it, bmp!!, false))
                                    }
                                    IMAGES = imgsList
                                }
                            }
                        }

                        action_apply_defaults.setOnClickListener {
                            txt_ai_health.setText("30")
                            txt_ai_armor.setText("0")
                            txt_ai_damage.setText("1")
                            txt_ai_spell_damage.setText("1")
                            txt_ai_draw.setText("3.0")
                            txt_ai_tackle.setText("5.0")
                            txt_ai_earth.setText("15")
                            txt_ai_wind.setText("15")
                            txt_ai_water.setText("15")
                            txt_ai_fire.setText("15")
                            txt_ai_light.setText("15")
                            txt_ai_dark.setText("15")
                            txt_ai_runes_min.setText("3")
                            txt_ai_runes_max.setText("5")
                        }

                        action_save_changes.setOnClickListener {
                            if (txt_ai_name.text.isNullOrEmpty()
                                || txt_ai_description.text.isNullOrEmpty()
                                || txt_ai_health.text.isNullOrEmpty()
                                || txt_ai_armor.text.isNullOrEmpty()
                                || txt_ai_damage.text.isNullOrEmpty()
                                || txt_ai_spell_damage.text.isNullOrEmpty()
                                || txt_ai_draw.text.isNullOrEmpty()
                                || txt_ai_tackle.text.isNullOrEmpty()
                                || IMAGES.filter { it.isActive == true }.size == 0
                                || txt_ai_earth.text.isNullOrEmpty()
                                || txt_ai_wind.text.isNullOrEmpty()
                                || txt_ai_water.text.isNullOrEmpty()
                                || txt_ai_fire.text.isNullOrEmpty()
                                || txt_ai_light.text.isNullOrEmpty()
                                || txt_ai_dark.text.isNullOrEmpty()
                                || txt_ai_runes_min.text.isNullOrEmpty()
                                || txt_ai_runes_max.text.isNullOrEmpty()){
                                Helper.toast(this@AICharacterActivity, "Form Incomplete, you may wan't to apply default values!")
                            }else{

                                var nai = AICharacter()
                                var base64 : String? = null
                                val dialog = this
                                async {
                                    var bmp = await { Helper.getBitmapFromUrl(IMAGES?.find { it.isActive == true }!!.imgUrl) }
                                    bmp = Helper.getResizedBitmap(bmp!!, 300)
                                    base64 = Helper.encodeImageToBase64(bmp!!)
                                    if (ai == null)
                                        nai._id = UUID.randomUUID().toString()
                                    else
                                        nai = ai

                                    API?.transact {
                                        nai.name = txt_ai_name.text.toString()
                                        nai.description = txt_ai_description.text.toString()
                                        nai.imgUrl = base64
                                        nai.initHealth = txt_ai_health.text.toString().toInt()
                                        nai.initArmor = txt_ai_armor.text.toString().toInt()
                                        nai.initDamage = txt_ai_damage.text.toString().toInt()
                                        nai.initSpellDamage = txt_ai_spell_damage.text.toString().toInt()
                                        nai.drawChance = txt_ai_draw.text.toString().toFloat()
                                        nai.tackleChance = txt_ai_tackle.text.toString().toFloat()
                                        nai.earth = txt_ai_earth.text.toString().toInt()
                                        nai.wind = txt_ai_wind.text.toString().toInt()
                                        nai.water = txt_ai_water.text.toString().toInt()
                                        nai.fire = txt_ai_fire.text.toString().toInt()
                                        nai.light = txt_ai_light.text.toString().toInt()
                                        nai.dark = txt_ai_dark.text.toString().toInt()
                                        nai.runeRangeMin = txt_ai_runes_min.text.toString().toInt()
                                        nai.runeRangeMax = txt_ai_runes_max.text.toString().toInt()

                                        if (ai == null)
                                            it.copyToRealm(nai)
                                        else
                                            it.insertOrUpdate(nai)
                                        dialog.dismiss()

                                        RECORDS = it.where(AICharacter::class.java).findAll()
                                    }
                                }

                            }

                        }
                    }
                }
            }

            OnlyHere().apply()
        }
    }

    private fun getRecords() {
        itemsswipetorefresh.isRefreshing = true
        API?.transact {
            RECORDS = it.where(AICharacter::class.java).findAll()
        }
        itemsswipetorefresh.isRefreshing = false
    }

    private fun populateList() {
        if (RECORDS == null)
            return

        rv_ais.layoutManager = LinearLayoutManager(this)
        ADAPTER = AiCharacterAdapter(this,RECORDS!!, onClick = {
            Helper.showConfirm(this,"Duel with ${it.name}?"){
                val i = Intent(this, BattleGroundActivity::class.java)
                i.putExtra("ai_id",it._id)
                startActivity(i)
            }
        }){
            initEditAi(it)
            true
        }
    }

    private fun initEditAi(ai: AICharacter) {
        showAiForm(ai)
    }

    private fun initDeleteAi(ai: AICharacter): Boolean {
        Helper.showConfirm(this, "Delete ${ai.name} AI?"){
            API?.transact {
                ai.deleteFromRealm()
            }
            getRecords()
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        API?.dispose()
    }

    var API : DataManager? = null

    var RECORDS : RealmResults<AICharacter>? = null
    set(value) {
        field = value
        if (value != null)
            populateList()
    }

    var ADAPTER : AiCharacterAdapter? = null
    set(value) {
        field = value
        rv_ais.adapter = value
    }

    class AiCharacterViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val image = view.img_thumb
        val name = view.txt_name
        val description = view.txt_description
        val container = view.cv_item_container
    }

    class AiCharacterAdapter(
        val ctx : Context,
        val items : RealmResults<AICharacter>,
        val onClick : (ai : AICharacter) -> Unit,
        val onLongClick : (ai : AICharacter) -> Boolean
    ) : RecyclerView.Adapter<AiCharacterViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiCharacterViewHolder {
            return AiCharacterViewHolder(LayoutInflater.from(ctx).inflate(R.layout.getlist_row_item,parent,false))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: AiCharacterViewHolder, position: Int) {
            holder.apply {
                val item = items.get(position)
                name.text = item!!.name
                description.text = item!!.description

                if (item!!.imgUrl?.startsWith("http") ?: false){
                    val url = item!!.imgUrl!!
                    async {
                        if (await { Helper.isInternetAvailable() }){
                            val bmp = await { Helper.getBitmapFromUrl(url) }
                            image.setImageBitmap(bmp!!)
                        }

                    }
                }else if(item!!.imgUrl != null){
                    val bmp = Helper.decodeBase64ToBitmap(item!!.imgUrl!!)
                    image.setImageBitmap(bmp)
                }
                container.setOnClickListener { onClick(item) }
                container.setOnLongClickListener {
                    onLongClick(item)
                }
            }
        }

    }
}