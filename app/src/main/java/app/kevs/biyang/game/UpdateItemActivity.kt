package app.kevs.biyang.game

import adapter.Hotel_page_travelum_adapter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.ItemImage
import app.kevs.biyang.game.libs.ItemImageAdapter
import app.kevs.biyang.game.libs.data.DataManager
import app.kevs.biyang.game.libs.models.GroceryItem
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.getlist_activity_update_item.*
import model.Hotel_page_travelum_model

class UpdateItemActivity : AppCompatActivity() {
    var dataManager : DataManager? = null
    val itemImages = listOf(R.drawable.aram, R.drawable.bad_room, R.drawable.bad1, R.drawable.aram)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getlist_activity_update_item)

        dataManager = DataManager(this)

        rc_imgs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rc_imgs.itemAnimator = DefaultItemAnimator()

        val models = ArrayList<Hotel_page_travelum_model>()
        itemImages.map {
            models.add(Hotel_page_travelum_model(it))
        }

        rc_imgs.adapter = Hotel_page_travelum_adapter(this, models)

        loadCategories()
        getItemData()

        action_back.setOnClickListener {
            finish()
        }

        action_save.setOnClickListener {
            attemptSave()
        }

        txt_name.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                loadRelatedImages()
            }
        }

        action_load_remark.setOnClickListener {
            confirmLoadDescription()
        }
    }

    private fun UpdateItemActivity.confirmLoadDescription() {
        Helper.showConfirm(this, "Load Description from Web?") {
            async {
                val remarks =
                    await { Helper.getDescriptionFromWikipedia(txt_name.text.toString().trim()) }
                txt_remarks.setText(remarks)
            }
        }
    }

    private fun attemptSave() {
        if (txt_name.text.isNullOrEmpty()){
            Helper.toast(this, "Item name is required", true)
            return
        }

        val new_category = txt_category.text

        var category : String? = null
        if (!new_category.isNullOrEmpty()){
            category = new_category.toString()
        }else{
            if (spinner_category.selectedItem != null)
                category = spinner_category.selectedItem.toString()
        }

        var imgUrl : String? = null

        if (IMAGES.size > 0){
            val img = IMAGES.filter { it.isActive }.first()
            if (img != null)
                imgUrl = img.imgUrl
        }

        val item = GroceryItem(
            ITEM?._id,
            txt_name.text.toString().trim(),
            0,
            txt_quantity.text.toString().trim(),
            category,
            txt_remarks.text.toString().trim(), null, imgUrl, false, 0
        )

        dataManager?.updateItem(item, onSuccess = {finish()}){handleError(it)}
    }

    private fun getItemData() {
        val id = intent.getStringExtra("ITEM_ID")
        if (id.isNullOrEmpty()){
            return
        }
        dataManager?.getItem(id,onSuccess = {ITEM = it}){handleError(it)}
    }

    private fun loadCategories() {
        dataManager?.getCategoriesFromItems(onSuccess = {CATEGORIES = it }){handleError(it)}
    }

    private fun handleError(error: Throwable) {
        Helper.toast(this, error.message!!)
    }

    private fun populateFields() {
        txt_name.setText(ITEM?.name)
        txt_quantity.setText("${ITEM?.quantityType ?: ""}")
        txt_remarks.setText(ITEM?.remarks ?: "")

        if (!ITEM?.category.isNullOrEmpty()){
            val adapter = CATEGORY_ADAPTER
            if (adapter != null)
                spinner_category.setSelection(adapter.getPosition(ITEM?.category))
        }

        loadRelatedImages()
    }

    private fun UpdateItemActivity.loadRelatedImages() {
        async {
            if (Helper.isNetworkConnected(this@UpdateItemActivity) && await { Helper.isInternetAvailable() }) {
                val urls =
                    await { Helper.getRelatedImagesUrlFromWeb(txt_name.text.toString().trim(), 20) }
                if (urls.size > 0) {
                    val bmps = ArrayList<ItemImage>()
                    if (!ITEM?.imgUrl.isNullOrEmpty()) {
                        val itemImgUrl = ITEM?.imgUrl
                        var bmp = await { Helper.getBitmapFromUrl(itemImgUrl!!) }
                        val itemImage = ItemImage(itemImgUrl!!, bmp!!, true)
                        bmps.add(itemImage)
                    }
                    urls.map {
                        val url = it
                        if (!url.equals(ITEM?.imgUrl)) {
                            val bmp = await { Helper.getBitmapFromUrl(url) }
                            if (bmp != null) {
                                val itemImage = ItemImage(url!!, bmp!!, false)
                                bmps.add(itemImage)
                            }
                        }
                    }
                    IMAGES = bmps
                }
            }

        }
    }

    var ITEM : GroceryItem? = null
        set(value) {
            field = value
            if (value != null){
                populateFields()
            }
        }

    var CATEGORIES = ArrayList<String>()
    set(value) {
        field = value
        if (value.size == 0){
            spinner_category.visibility = View.GONE
        }
        CATEGORY_ADAPTER = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, value)
    }

    var CATEGORY_ADAPTER : ArrayAdapter<String>? = null
    set(value) {
        field = value
        spinner_category.adapter = value
    }

    var IMAGES = ArrayList<ItemImage>()
    set(value) {
        field = value
        rc_imgs.adapter = ItemImageAdapter(this@UpdateItemActivity, value){
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
        rc_imgs.visibility = View.VISIBLE
    }
}