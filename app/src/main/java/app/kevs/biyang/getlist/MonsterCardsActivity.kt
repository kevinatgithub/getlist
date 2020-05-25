package app.kevs.biyang.getlist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.getlist.libs.Helper
import app.kevs.biyang.getlist.libs.TravelumHelper
import app.kevs.biyang.getlist.libs.adapters.AdapterType
import app.kevs.biyang.getlist.libs.adapters.ImageGridAdapter
import app.kevs.biyang.getlist.libs.adapters.MonsterCardAdapter
import app.kevs.biyang.getlist.libs.data.DataManager
import app.kevs.biyang.getlist.libs.models.MonsterCard
import app.kevs.biyang.getlist.libs.models.SpellBook
import co.metalab.asyncawait.async
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_monstcards.*
import kotlinx.android.synthetic.main.game_dialog_image_change.*
import kotlinx.android.synthetic.main.game_dialog_image_preview.*
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*
import java.util.*

class MonsterCardsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_monstcards)

        api = DataManager(this)

        getCards()

        itemsswipetorefresh.setOnRefreshListener {
            getCards()
        }

        txt_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                CARDS = UNFILTERED_CARDS?.filter { it.name?.toUpperCase()?.contains(s.toString().toUpperCase())!! }
                CARDS_ADAPTER?.notifyDataSetChanged()
            }

        })

        action_create_card.setOnClickListener {
            createCard()
        }

        action_menu.setOnClickListener {
            VIEW_TYPE = when(VIEW_TYPE){
                AdapterType.LIST -> AdapterType.GRID
                AdapterType.GRID -> AdapterType.LIST
                else -> AdapterType.LIST
            }
        }

        action_expand.setOnClickListener {
            initMultiEntry()
        }

        action_select_category.setOnClickListener {
            val categories = UNFILTERED_CARDS?.map { it.category ?: "Not Set" }?.distinct()
            Helper.promptSpinner(this,"","Select Category",categories!!){
                if (it.equals("Not Set")){
                    CARDS = UNFILTERED_CARDS?.filter { it.category.isNullOrEmpty() }
                }else{
                    val uc = UNFILTERED_CARDS?.filter { !it.category.isNullOrEmpty() }
                    CARDS = uc?.filter { it.category?.equals(it)!! }
                }
                CARDS_ADAPTER?.notifyDataSetChanged()
            }
        }

        img_menu.setOnClickListener { startActivity(Intent(this, BattleGroundActivity::class.java)) }
    }

    private fun initMultiEntry() {
        Helper.prompt(this,"", "Enter names in different lines"){
            itemsswipetorefresh.isRefreshing = true
            val parts = it.split("\n")
            parts.forEach {
                val name = it.trim()
                async {
                    val imgs = await { Helper.getRelatedImagesUrlFromWeb(name,1) }
                    val description = await {Helper.getDescriptionFromWikipedia(name)}
                    val record = MonsterCard(
                        UUID.randomUUID().toString(),
                        name,
                        description,
                        imgs[0],
                        null
                    )
                    api?.createCard(record){

                    }
                    getCards()
                    itemsswipetorefresh.isRefreshing = false
                }
            }
        }
    }

    private fun createCard() {
        if (txt_name.text.isNullOrEmpty()){
            return
        }

        val skillNames = SpellBook.spells.map { it.first }
        val randomSkillIndex = (0..skillNames.size-1).random()
        val cardSkill = skillNames[randomSkillIndex]
        val name = txt_name.text.toString().capitalize()
        txt_name.setText("")
        itemsswipetorefresh.isRefreshing = true
        async {
            val images = await { Helper.getRelatedImagesUrlFromWeb(name,1) }
            var url = images[0]
//            var bmp = await { Helper.getBitmapFromUrl(url) }
//            url = Helper.bitmapToFile(name, bmp!!)

            var description = await { Helper.getDescriptionFromWikipedia(name)}
            if (description == null){
                description = await { Helper.getDescriptionFromWikipedia("entity") }
            }

            val record = MonsterCard(
                UUID.randomUUID().toString(),
                name,
                description,
                url,
                cardSkill
            )
            api?.createCard(record){
                itemsswipetorefresh.isRefreshing = false
                getCards()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        api?.dispose()
    }

    private fun getCards(){
        itemsswipetorefresh.isRefreshing = true
        api?.getCards {
            UNFILTERED_CARDS = it
            CARDS = it
//            UNFILTERED_CARDS?.forEach {
//                api?.transact {
//                    it.category = "England"
//                }
//            }
            itemsswipetorefresh.isRefreshing = false
        }
    }

    private fun populateList() {
        if (CARDS == null){
            return
        }
        rv_cards.layoutManager = when(VIEW_TYPE){
            AdapterType.LIST -> LinearLayoutManager(this)
            AdapterType.GRID -> GridLayoutManager(this, 3)
            else -> GridLayoutManager(this, 3)
        }

        CARDS_ADAPTER = MonsterCardAdapter(this, CARDS!!, VIEW_TYPE,
            onRowClick = {
                openCardPreview(it)
            },
            onRowLongClick = {

            })
    }

    private fun openCardPreview(card: MonsterCard) {
        var categories = UNFILTERED_CARDS?.map { it.category ?: "" }
        categories = categories?.distinct()
        categories = categories?.filter { it != "" }
        categories = categories?.plus("New Category")
        val spells = SpellBook.spells
        val spellNames = spells.map { it.first }
        val selectedSpells = UNFILTERED_CARDS?.map { it.skill }
        val availableSpells = spellNames.filter {
            !selectedSpells?.contains(it)!!
        }
        Helper.toast(this, availableSpells.size.toString())
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
                lbl_description.setText("${card.skill ?: "Not Set"}\n${lbl_description.text}")
                lbl_description.setOnClickListener {
                    this.dismiss()
//                    Helper.promptSpinner(this@MonsterCardsActivity,"","Select Spell",spellNames){
//                        api?.transact { card.skill = it.toUpperCase() }
//                        CARDS_ADAPTER?.notifyDataSetChanged()
//                    }
                    val rand = (0..availableSpells.size-1).random()
                    api?.transact {
                         card.skill = availableSpells[rand]
                    }
                    CARDS_ADAPTER?.notifyDataSetChanged()
                }
                lbl_name.setText(card.name)
                lbl_category.setText(card.category ?: "Not set")
                val url = card.imgUrl

                img_thumb.setBackgroundResource(R.drawable.img3)
                img_thumb.setOnClickListener {
                    openImagePreview(card.imgUrl!!){
                        this.dismiss()
                        initChangeThumbnail(card)
                    }
                }
                async {
                    if (!card.imgUrl.isNullOrEmpty() && await { Helper.isInternetAvailable() }){
                        if (card.imgUrl!!.startsWith("http")){
                            val url = card.imgUrl
                            async {
                                val bmp = await {Helper.getBitmapFromUrl(url!!)}
                                img_thumb.setImageBitmap(bmp)
                            }
                        }else{
                            Helper.fileToImageView(img_thumb, card.imgUrl!!)
                        }
                    }else{
                        img_thumb.setImageDrawable(resources.getDrawable(R.drawable.img3))
                    }
                }

                val dialog = this
                action_complete.apply {
                    text = "Set Category"
                    setOnClickListener {
                        dialog.dismiss()
                        selectCategory(card, categories)
                    }
                }
                action_remove.setOnClickListener {
                    this.dismiss()
                    Helper.showConfirm(this@MonsterCardsActivity, "Delete Card?"){
                        api?.deleteCard(card._id!!){
                            getCards()
//                            CARDS_ADAPTER?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun initChangeThumbnail(card: MonsterCard) {
        val name = card.name
        TravelumHelper.ShowDialog(this, R.layout.game_dialog_image_change, 0.7){
            var images : List<String>? = null
            async {
                images = await { Helper.getRelatedImagesUrlFromWeb(name!!, 20) }
                it.apply {
                    lv_img_choices.layoutManager = GridLayoutManager(this@MonsterCardsActivity, 2)
                    lv_img_choices.adapter = ImageGridAdapter(this@MonsterCardsActivity, images!!){
                        this.dismiss()
                        api?.transact {
                            card.imgUrl = it
                        }
                        CARDS_ADAPTER?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun openImagePreview(imgUrl: String, onChangeThumb : () -> Unit) {
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_image_preview,0.8){
            val dialog = it
            it.apply {
                async {
                    if (imgUrl.startsWith("http")){
                        val bmp = await { Helper.getBitmapFromUrl(imgUrl) }
                        img_preview.setImageBitmap(bmp)
                    }else{
                        Helper.fileToImageView(img_preview, imgUrl)
                    }
                }

                img_preview_container.setOnClickListener {
                    dialog.dismiss()
                }

                action_change_thumbnail.setOnClickListener {
                    dialog.dismiss()
                    onChangeThumb()
                }
            }

        }
    }

    private fun selectCategory(
        card: MonsterCard,
        categories: List<String>?
    ) {
        Helper.promptSpinner(
            this@MonsterCardsActivity, "",
            "Select Category", categories!!
        ) {
            if (it.equals("New Category")) {
                Helper.prompt(this@MonsterCardsActivity, "", "Enter New Category") {
                    api?.transact {
                        card.category = it
                    }
                }
            } else {
                api?.transact {
                    card.category = it
                }
            }
            CARDS_ADAPTER?.notifyDataSetChanged()
        }
    }

    var VIEW_TYPE : AdapterType? = AdapterType.LIST
    set(value) {
        field = value
        populateList()
    }

    var CARDS_ADAPTER : MonsterCardAdapter? = null
    set(value) {
        field = value
        rv_cards.adapter = value
    }

    var UNFILTERED_CARDS : RealmResults<MonsterCard>? = null
    var CARDS : List<MonsterCard>? = null
    set(value) {
        field = value
        if (value != null)
            populateList()
    }

    var api : DataManager? = null
}