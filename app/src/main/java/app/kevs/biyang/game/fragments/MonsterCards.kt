package app.kevs.biyang.game.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.game.R
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
import kotlinx.android.synthetic.main.game_activity_monstcards.action_create_card
import kotlinx.android.synthetic.main.game_activity_monstcards.action_download
import kotlinx.android.synthetic.main.game_activity_monstcards.action_expand
import kotlinx.android.synthetic.main.game_activity_monstcards.action_select_category
import kotlinx.android.synthetic.main.game_activity_monstcards.action_upload
import kotlinx.android.synthetic.main.game_activity_monstcards.itemsswipetorefresh
import kotlinx.android.synthetic.main.game_activity_monstcards.rv_cards
import kotlinx.android.synthetic.main.game_activity_monstcards.txt_name
import kotlinx.android.synthetic.main.game_dialog_image_change.*
import kotlinx.android.synthetic.main.game_dialog_image_preview.*
import kotlinx.android.synthetic.main.game_fragment_monstercards.*
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*
import java.util.*

class MonsterCards : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment_monstercards,container,false)
    }

    var mActivity : FragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity //as DashboardActivity
        mActivity!!.setContentView(R.layout.game_fragment_monstercards)
//        mActivity!!.setContentView(view)
        api = DataManager(context!!)

        mActivity!!.apply {

            getCards()

            itemsswipetorefresh.setOnRefreshListener {
                getCards()
            }

            txt_name.addTextChangedListener(object : TextWatcher {
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

            action_change_listtype.setOnClickListener {
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
                Helper.promptSpinner(context!!,"","Select Category",categories!!){
                    if (it.equals("Not Set")){
                        CARDS = UNFILTERED_CARDS?.filter { it.category.isNullOrEmpty() }
                    }else{
                        val uc = UNFILTERED_CARDS?.filter { !it.category.isNullOrEmpty() }
                        CARDS = uc?.filter { it.category?.equals(it)!! }
                    }
                    CARDS_ADAPTER?.notifyDataSetChanged()
                }
            }

//            img_menu.setOnClickListener { startActivity(Intent(context, BattleGroundActivity::class.java)) }

            action_upload.setOnClickListener {
                async {
                    if (Helper.isNetworkConnected(context!!) && await { Helper.isInternetAvailable() }){
                        Helper.showConfirm(context!!,"Upload Data without Images?"){
                            uploadData()
                        }
                    }else{
                        Helper.toast(context!!, "Internet not available")
                    }
                }
            }

            action_download.setOnClickListener {
                async {
                    if (Helper.isNetworkConnected(context!!) && await { Helper.isInternetAvailable() }){
                        Helper.showConfirm(context!!,"Clear local data and download?"){
                            downloadData()
                        }
                    }else{
                        Helper.toast(context!!, "Internet not available")
                    }
                }
            }
        }

    }

    private fun downloadData() {
        mActivity!!.apply {
            api?.clearCards {
                api?.downloadData(
                    onResult = {
                        val dialog = AlertDialog.Builder(context!!)
                        val pb = ProgressBar(context!!, null, android.R.attr.progressBarStyleHorizontal)
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
                ){ Helper.toast(context!!,it.message.toString(),true)}
            }
        }
    }

    private fun uploadData() {
        mActivity!!.apply {
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
                        Helper.toast(context!!, "Data Uploaded")
                    }){ Helper.toast(context!!, it.message.toString(),true)}
            }
        }
    }

    private fun initMultiEntry() {
        mActivity!!.apply {

            Helper.prompt(context!!,"", "Enter names in different lines"){
                itemsswipetorefresh.isRefreshing = true
                val parts = it.split("\n")
                parts.forEach {
                    val name = it.trim()
                    async {
                        val imgs = await { Helper.getRelatedImagesUrlFromWeb(name,1) }
                        val description = await { Helper.getDescriptionFromWikipedia(name)}
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
    }

    private fun createCard() {
        mActivity!!.apply {

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
                var bmp = await { Helper.getBitmapFromUrl(url) }
                url = Helper.encodeImageToBase64(Helper.getResizedBitmap(bmp!!, 300)!!)!!
    //            url = Helper.saveImage(context!!, bmp!!, name).toString()

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
    }

    override fun onPause() {
        super.onPause()
        api?.dispose()
    }

    private fun getCards(){
        mActivity!!.apply {
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
    }

    private fun populateList() {
        mActivity!!.apply {


            if (CARDS == null){
                return
            }
            rv_cards.layoutManager = when(VIEW_TYPE){
                AdapterType.LIST -> LinearLayoutManager(context!!)
                AdapterType.GRID -> GridLayoutManager(context!!, 3)
                else -> GridLayoutManager(context!!, 3)
            }

            val spells = SpellBook.getSpells(api!!)

            CARDS_ADAPTER = MonsterCardAdapter(context!!, CARDS!!, spells, VIEW_TYPE,
                onRowClick = {
                    openCardPreview(it)
                },
                onRowLongClick = {

                })

        }
    }

    private fun openCardPreview(card: MonsterCard) {
        mActivity!!.apply {


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
            TravelumHelper.ShowDialog(context!!, R.layout.getlist_dialog_item_preview,0.7){
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
                        Helper.promptSpinner(context!!,"Encanto","Select from unassigned spells",availableSpells){
                            val spell = it
                            api?.transact { card.skill = spell.toUpperCase() }
                            CARDS_ADAPTER?.notifyDataSetChanged()
                        }
    //                    val rand = (0..availableSpells.size-1).random()
    //                    api?.transact {
    //                         card.skill = availableSpells[rand]
    //                    }
    //                    CARDS_ADAPTER?.notifyDataSetChanged()
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
                                    val bmp = await { Helper.getBitmapFromUrl(url!!)}
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
                        text = "Set Category"
                        setOnClickListener {
                            dialog.dismiss()
                            selectCategory(card, categories)
                        }
                    }
                    action_remove.setOnClickListener {
                        this.dismiss()
                        Helper.showConfirm(context!!, "Delete Card?"){
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
                            CARDS = UNFILTERED_CARDS?.filter { it.name?.toUpperCase()?.contains(txt_name.text.toString().toUpperCase())!! }
                        }
                    }
                }
            }

        }
    }

    private fun initChangeThumbnail(card: MonsterCard) {
        mActivity!!.apply {

            val name = card.name
            TravelumHelper.ShowDialog(context!!, R.layout.game_dialog_image_change, 0.7){
                var images : List<String>? = null
                async {
                    images = await { Helper.getRelatedImagesUrlFromWeb(name!!, 20) }
                    it.apply {
                        lv_img_choices.layoutManager = GridLayoutManager(context!!, 2)
                        lv_img_choices.adapter = ImageGridAdapter(context!!, images!!){
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
    }

    private fun openImagePreview(imgUrl: String, onChangeThumb : () -> Unit) {
        mActivity!!.apply {

            TravelumHelper.ShowDialog(context!!, R.layout.game_dialog_image_preview,0.8){
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
    }

    private fun selectCategory(
        card: MonsterCard,
        categories: List<String>?
    ) {
        mActivity!!.apply {

            Helper.promptSpinner(
                context!!, "",
                "Select Category", categories!!
            ) {
                if (it.equals("New Category")) {
                    Helper.prompt(context!!, "", "Enter New Category") {
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
    }

    var VIEW_TYPE : AdapterType? = AdapterType.LIST
        set(value) {
            field = value
            populateList()
        }

    var CARDS_ADAPTER : MonsterCardAdapter? = null
        set(value) {
            field = value
            mActivity!!.rv_cards.adapter = value
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