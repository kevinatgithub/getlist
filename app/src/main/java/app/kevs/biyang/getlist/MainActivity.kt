package app.kevs.biyang.getlist

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.kevs.biyang.getlist.libs.GroceryItemAdapter
import app.kevs.biyang.getlist.libs.Helper
import app.kevs.biyang.getlist.libs.TravelumHelper
import app.kevs.biyang.getlist.libs.data.DataManager
import app.kevs.biyang.getlist.libs.models.GroceryItem
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.getlist_activity_main.*
import kotlinx.android.synthetic.main.getlist_dialog_item_preview.*
import kotlinx.android.synthetic.main.getlist_dialog_main_menu.*


class MainActivity : AppCompatActivity() {

    companion object{
        var APP_MODE = MODE.OFFLINE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getlist_activity_main)

        dataManager = DataManager(this)

        refreshList()

        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            refreshList()
            itemsswipetorefresh.isRefreshing = false
        }

        action_save_item.setOnClickListener {
            if (!txt_name.text.isNullOrEmpty()){
                createNewItem()
            }
        }

        action_menu.setOnClickListener {
            TravelumHelper.ShowDialog(this,
                R.layout.getlist_dialog_main_menu){
                applyMenuHandler(it)
            }
        }

        refreshAppLabel()

        txt_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                performSearch()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun performSearch() {
        val newList = GROCERY_LISTS_ORIGINAL.filter { it.name.toString().toUpperCase().contains(txt_name.text.toString().toUpperCase()) }
        val newArrayList = ArrayList<GroceryItem>()
        newArrayList.addAll(newList)
        GROCERY_LISTS = newArrayList
    }

    private fun refreshAppLabel(){
        when(APP_MODE){
            MODE.ONLINE -> txt_app_status.text = "App is Online"
            MODE.OFFLINE -> txt_app_status.text = "App is Offline"
        }
    }

    private fun applyMenuHandler(dialog: Dialog) {
        dialog?.apply {
            ll_sync_data.setOnClickListener {
                dismiss()
                val source = when(MainActivity.APP_MODE){
                    MODE.ONLINE -> MODE.OFFLINE
                    MODE.OFFLINE -> MODE.ONLINE
                }
                Helper.showConfirm(this@MainActivity,"Sync data to $source"){
                    dataManager?.syncList(GROCERY_LISTS,
                        onSuccess = {
                            refreshList()
                            val copylocation = if (APP_MODE == MODE.ONLINE) {
                                MODE.OFFLINE
                            }else{
                                MODE.ONLINE
                            }
                            Helper.toast(this@MainActivity, "Copied to $copylocation")
                        },
                        onError = {handleError(it)})
                }
            }

            ll_switch_mode.setOnClickListener {
                dismiss()
                APP_MODE = when(APP_MODE){
                    MODE.ONLINE -> MODE.OFFLINE
                    MODE.OFFLINE -> MODE.ONLINE
                }
                refreshAppLabel()
                refreshList()
            }

            ll_clear_list.setOnClickListener {
                dismiss()
                Helper.showConfirm(this@MainActivity, "Clear all items?"){
                    dataManager?.clearAll(onSuccess = {refreshList()}){handleError(it)}
                }
            }

            ll_save_list.setOnClickListener {
                dismiss()
                Helper.prompt(this@MainActivity,"New List", """
                    Enter new list name
                    IMPORTANT! This will delete all items in an existing list
                """.trimIndent()){
                    dataManager?.assignList(it,onSuccess = {
                        Helper.toast(this@MainActivity, "Saved to list")
                    }){handleError(it)}
                }
            }

            ll_load_list.setOnClickListener {dismiss();startActivity(Intent(this@MainActivity, LoadListActivity::class.java))}
        }
    }

    override fun onPause() {
        super.onPause()
        dataManager?.dispose()
    }

    override fun onPostResume() {
        super.onPostResume()
        refreshList()
    }

    private fun createNewItem() {
        Helper.toast(this, "Saving..")
        val itemName = txt_name.text.toString().trim()
        txt_name.setText("")

        async {
            var imgUrl : String? = null
            var remarks : String? = null
            if (Helper.isNetworkConnected(this@MainActivity) && await {Helper.isInternetAvailable()}){
                val imgResult = await { Helper.getRelatedImagesUrlFromWeb(itemName, 1) }
                if (imgResult.size > 0)
                    imgUrl = imgResult.get(0)

                remarks = await { Helper.getDescriptionFromWikipedia(itemName) }
            }
            dataManager?.addItem(
                GroceryItem(
                    null,
                    itemName,
                    1,null,null,remarks,null,imgUrl, false,0
                ),
                onSuccess = {
                    refreshList()
                    txt_name.setText("")
                },
                onError = {handleError(it)}
            )
            onError { handleError(it) }
        }
    }

    private fun refreshList(){
        setState(State.LOADING)

        dataManager?.getList(
            onSuccess = {
                GROCERY_LISTS_ORIGINAL = it
                GROCERY_LISTS = it
            },
            onError = {
                handleError(it)
            }
        )

    }

    private fun populateList() {
        if (GROCERY_LISTS.size > 0){
            setState(State.HAS_RESULT)
            val sorted = GROCERY_LISTS?.sortedWith(compareBy({it.category}, {it.category}))
            val items = ArrayList<GroceryItem>()
            if (sorted != null){
                items.addAll(sorted)
            }

            rc_items.layoutManager = LinearLayoutManager(this)
            rc_items.adapter = GroceryItemAdapter(this, items, onRowClick = {
                openItemPreview(it)
            }){
                Helper.toast(this, "Feature not available")
            }
        }else{
            setState(State.HAS_NO_RESULT)
        }
    }

    private fun openItemPreview(item: GroceryItem) {
        TravelumHelper.ShowDialog(this, R.layout.getlist_dialog_item_preview,0.600){

            it.apply {
                lbl_name.text = item.name?.capitalize()
                val len = item.remarks?.length ?: 0
                val maxlen = if (len >= 400){
                    400
                }else{
                    len
                }
                lbl_description.text = """
                    ${item.quantityType ?: "no quantity"} | ${item.category ?: "no category"} 
                    ${item.remarks?.substring(0,maxlen) ?: "No remarks"}
                """.trimIndent()
                img_thumb.setBackgroundDrawable(resources.getDrawable(R.drawable.img3))

                async {
                    if (!item.imgUrl.isNullOrEmpty() && await { Helper.isInternetAvailable() }){
                        val url = item.imgUrl
                        async {
                            val bmp = await {Helper.getBitmapFromUrl(url!!)}
                            img_thumb.setImageBitmap(bmp)
                        }
                    }else{
                        img_thumb.setImageDrawable(resources.getDrawable(R.drawable.img3))
                    }

                }

                if (item.isComplete != null){
                    if (item.isComplete!!){
                        action_complete.visibility = View.INVISIBLE
                    }
                }

                action_complete.setOnClickListener {
                    dismiss()
                    Helper.showConfirm(this@MainActivity, "Mark Item as Completed?"){
                        dataManager?.flagItemAsComplete(item,
                            onSuccess = {}){handleError(it)}
                    }
                }

                action_update.setOnClickListener {
                    val intent = Intent(this@MainActivity, UpdateItemActivity::class.java)
                    intent.putExtra("ITEM_ID", item._id)
                    startActivity(intent)
                    this.dismiss()
                }

                action_remove.setOnClickListener {
                    Helper.showConfirm(this@MainActivity, "Remove item from list?"){
                        dataManager?.deleteItem(item, onSuccess = {
                            refreshList()
                            performSearch()
                        }){handleError(it)}
                    }
                    this.dismiss()
                }
            }

        }
    }

    private fun setState(state: State){
        when(state){
            State.LOADING ->{
                rc_items.visibility = View.GONE
                itemsswipetorefresh.isRefreshing = true
                /*img_info.visibility = View.VISIBLE
                lbl_info.apply{
                    visibility = View.VISIBLE
                    text = "Please wait, loading.."
                }*/
            }
            State.HAS_RESULT ->{
                itemsswipetorefresh.isRefreshing = false
                rc_items.visibility = View.VISIBLE
                /*lbl_info.visibility = View.GONE
                img_info.visibility = View.GONE*/
            }
            State.HAS_NO_RESULT ->{
                itemsswipetorefresh.isRefreshing = false
                rc_items.visibility = View.GONE
                /*lbl_info.apply{
                    visibility = View.VISIBLE
                    text = "No items to display, click + to create"
                }*/
            }
        }
    }

    private fun handleError(error : Throwable) {
        Helper.toast(this,"error : ${error.message}",true)
    }

    // PROPERTIES

    var dataManager : DataManager? = null

    var GROCERY_LISTS_ORIGINAL = ArrayList<GroceryItem>()
    var GROCERY_LISTS = ArrayList<GroceryItem>()
        set(value) {
            val filtered = ArrayList<GroceryItem>()
            filtered.addAll(value.filter {
                !it.isComplete!!
            })
            field = if (COMPLETED_VISIBLE){
                value
            }else{
                filtered
            }

            if (value != null){
                populateList()
            } else {
                setState(State.HAS_NO_RESULT)
                Helper.toast(this, "No items yet")
            }
        }

    var COMPLETED_VISIBLE = true
}

enum class MODE {
    ONLINE, OFFLINE
}

enum class State{
    LOADING, HAS_RESULT, HAS_NO_RESULT
}
