package app.kevs.biyang.game

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.TravelumHelper
import app.kevs.biyang.game.libs.adapters.AdapterType
import app.kevs.biyang.game.libs.adapters.ImageGridAdapter
import app.kevs.biyang.game.libs.adapters.MonsterCardAdapter
import app.kevs.biyang.game.libs.data.DataManager
import app.kevs.biyang.game.libs.models.MonsterCard
import app.kevs.biyang.game.libs.models.SpellBook
import co.metalab.asyncawait.async
import com.google.gson.Gson
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_monstcards.*
import kotlinx.android.synthetic.main.game_dialog_image_change.*
import kotlinx.android.synthetic.main.game_dialog_image_preview.*
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*
import java.util.*
import kotlin.math.log

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

        action_upload.setOnClickListener {
            async {
                if (Helper.isNetworkConnected(this@MonsterCardsActivity) && await { Helper.isInternetAvailable() }){
                    Helper.showConfirm(this@MonsterCardsActivity,"Upload Data without Images?"){
                        uploadData()
                    }
                }else{
                    Helper.toast(this@MonsterCardsActivity, "Internet not available")
                }
            }
        }

        action_download.setOnClickListener {
            async {
                if (Helper.isNetworkConnected(this@MonsterCardsActivity) && await { Helper.isInternetAvailable() }){
                    Helper.showConfirm(this@MonsterCardsActivity,"Clear local data and download?"){
                        downloadData()
                    }
                }else{
                    Helper.toast(this@MonsterCardsActivity, "Internet not available")
                }
            }
        }
    }

    private fun downloadData() {
        api?.clearCards {
            api?.downloadData(
                onResult = {
                    val dialog = AlertDialog.Builder(this)
                    val pb = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
                    pb.apply { isIndeterminate = false; progress = 0; max = it.cards.size }
                    dialog.apply { setView(pb); setCancelable(false) }
                    val d = dialog.show()

                    it.cards.forEach {
                        async {
                            val urls = await { Helper.getRelatedImagesUrlFromWeb(it.name!!) }
                            val bmp = await { Helper.getBitmapFromUrl(urls[0]) }
                            val imgUrl = Helper.encodeImageToBase64(Helper.getResizedBitmap(bmp!!, 300)!!)
                            it.imgUrl = imgUrl
                            pb.progress = pb.progress+1
                            if(pb.progress >= pb.max){
                                d.dismiss()
                            }
                            api?.createCard(it){

                            }
                        }
                    }
//                    d.dismiss()
                }
            ){ Helper.toast(this,it.message.toString(),true)}
        }
    }

    private fun uploadData() {
        val cards = api?.getCards {
            val lCards = ArrayList<MonsterCard>()
            it.forEach {
                lCards.add(
                    MonsterCard(
                        it._id, it.name, it.description, null, it.skill, it.category
                    )
                )
            }
            val strCards = Gson().toJson(lCards)
            api?.uploadData(strCards, strCards, strCards,
                onResult = {
                    Helper.toast(this, "Data Uploaded")
                }){ Helper.toast(this, it.message.toString(),true)}
        }
    }

    private fun initMultiEntry() {
        val skillNames = SpellBook.getSpells(api!!).map { it.name }
        Helper.prompt(this,"", "Enter SCP numbers in different lines"){
            itemsswipetorefresh.isRefreshing = true
            val parts = it.split("\n")
            parts.forEach {
                val randomSkillIndex = (0..skillNames.size-1).random()
                val cardSkill = skillNames[randomSkillIndex]
                val name = it.trim()
                async {
                    val imgs = await { Helper.getRelatedImagesUrlFromWeb(name,1) }
                    val description = await {Helper.getDescriptionFromWikipedia(name)}
                    var url = imgs[0]
                    var bmp = await { Helper.getBitmapFromUrl(url) }
                    if (bmp != null){
                        url = Helper.encodeImageToBase64(Helper.getResizedBitmap(bmp!!, 300)!!)!!
                    }else{
                        url = Helper.encodeImageToBase64(resources.getDrawable(R.drawable.ic_info).toBitmap(300,300))!!
                    }
                    val record = MonsterCard(
                        UUID.randomUUID().toString(),
                        name,
                        description,
                        url,
                        cardSkill
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

        val skillNames = SpellBook.getSpells(api!!).map { it.name }
        val randomSkillIndex = (0..skillNames.size-1).random()
        val cardSkill = skillNames[randomSkillIndex]
        val name = txt_name.text.toString().capitalize()
        txt_name.setText("")
        itemsswipetorefresh.isRefreshing = true
        async {
            val images = await { Helper.getRelatedImagesUrlFromWeb(name,1) }
            var url = images[0]
            try {
                var bmp = await { Helper.getBitmapFromUrl(url) }
                url = Helper.encodeImageToBase64(Helper.getResizedBitmap(bmp!!, 300)!!)!!
            } catch (ex : Exception) {
                Log.e("scperror",ex.message);
            }
//            url = Helper.saveImage(this@MonsterCardsActivity, bmp!!, name).toString()

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
            CARDS = if (txt_name.text.isNullOrEmpty())
                UNFILTERED_CARDS
            else
                UNFILTERED_CARDS?.filter { it.name?.toUpperCase()?.contains(txt_name.text.toString().toUpperCase())!! }

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

        val spells = SpellBook.getSpells(api!!)
        CARDS_ADAPTER = MonsterCardAdapter(this, CARDS!!, spells, VIEW_TYPE,
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
        val spells = SpellBook.getSpells(api!!)
        val spellNames = spells.map { it.name }
        val selectedSpells = UNFILTERED_CARDS?.map { it.skill }
        val availableSpells = spellNames.filter {
            !selectedSpells?.contains(it.toUpperCase())!!
        }
//        Helper.toast(this, availableSpells.size.toString())
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
                    openDescription(card.description)
                }
                lbl_name.setText(card.name)
                lbl_category.setText(card.category ?: "Not set")
                val url = card.imgUrl

                img_thumb.setBackgroundResource(R.drawable.img3)
                img_thumb.setOnClickListener {
                    if (!card.imgUrl.isNullOrEmpty()){
                        openImagePreview(card.imgUrl!!){
                            this.dismiss()
                            initChangeThumbnail(card)
                        }
                    }
                }
                async {
                    if (!card.imgUrl.isNullOrEmpty()){
                        if (card.imgUrl!!.startsWith("http") && await { Helper.isInternetAvailable() }){
                            val url = card.imgUrl
                            async {
                                val bmp = await {Helper.getBitmapFromUrl(url!!)}
                                img_thumb.setImageBitmap(bmp)
                            }
                        }else{
                            img_thumb.setImageBitmap(Helper.decodeBase64ToBitmap(card.imgUrl!!))
                        }
                    }else{
                        img_thumb.setImageDrawable(resources.getDrawable(R.drawable.img3))
                    }
                }

                val dialog = this
                action_complete.apply {
                    text = "Show Description"
//                    text = "Set Category"
                    setOnClickListener {
                        openDescription(card.description)
//                        dialog.dismiss()
//                        selectCategory(card, categories)
                    }
                }
                action_remove.setOnClickListener {
                    this.dismiss()
                    Helper.showConfirm(this@MonsterCardsActivity, "Delete Card?"){
                        api?.deleteCard(card._id!!){
                            getCards()

                        }
                    }
                }

                action_update.setText("Create Copy")
                action_update.setOnClickListener {
                    this.dismiss()
                    val nCard = MonsterCard(UUID.randomUUID().toString(), card.name, card.description, card.imgUrl, card.skill, card.category)
                    api?.createCard(nCard){
                        CARDS_ADAPTER?.notifyDataSetChanged()
                        CARDS = UNFILTERED_CARDS?.filter { it.name?.toUpperCase()?.contains(this@MonsterCardsActivity.txt_name.text.toString().toUpperCase())!! }
                    }
                }
            }
        }
    }

    private fun openDescription(description: String?) {
        TravelumHelper.ShowDialog(this,R.layout.getlist_dialog_item_preview,1.0){
            it.apply {
                lbl_name.visibility = GONE;
                lbl_category.visibility = GONE;
                img_thumb.visibility = GONE;
                lbl_description.setText(description)
                action_complete.visibility = GONE;
                action_remove.visibility = GONE;
                action_update.visibility = GONE;
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
                        val bmp = it
                        api?.transact {
                            card.imgUrl = Helper.encodeImageToBase64(Helper.getResizedBitmap(bmp, 300)!!)
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
                        img_preview.setImageBitmap(Helper.decodeBase64ToBitmap(imgUrl))
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
                    val category = it
                    api?.transact {
                        card.category = category
                    }
                }
            } else {
                val category = it
                api?.transact {
                    card.category = category
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