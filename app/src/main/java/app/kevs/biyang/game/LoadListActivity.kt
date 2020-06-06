package app.kevs.biyang.game

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.data.DataManager
import kotlinx.android.synthetic.main.getlist_activity_loadlist.*

class LoadListActivity : AppCompatActivity() {
    var dataManager : DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getlist_activity_loadlist)

        dataManager = DataManager(this)

        action_back.setOnClickListener {
            finish()
        }

        itemsswipetorefresh.setOnRefreshListener {
            refreshList()
            itemsswipetorefresh.isRefreshing = false
        }

        lv_listnames.setOnItemClickListener { parent, view, position, id ->
            val listName = ITEMS?.get(position).toString()
            Helper.showConfirm(this,"Load ${listName}?"){loadList(listName)}
        }

        lv_listnames.setOnItemLongClickListener { parent, view, position, id ->
            val listName = ITEMS?.get(position).toString()
            Helper.showConfirm(this,"Delete ${listName}?"){
                dataManager?.deleteList(listName,onSuccess = {refreshList()}){handleError(it)}
            }
            true
        }

        refreshList()
    }

    private fun loadList(listName: String) {
        dataManager?.clearAll(onSuccess = {
            dataManager?.getItemsFromList(listName, onSuccess = {
                dataManager?.setList(it,onSuccess = {
                    finish()
                }){handleError(it)}
            }){handleError(it)}
        }){handleError(it)}
    }

    override fun onPause() {
        super.onPause()
        dataManager?.dispose()
    }

    private fun refreshList() {
        dataManager?.getListNames(onSuccess = {ITEMS = it}){handleError(it)}
    }

    private fun handleError(error: Throwable) {
        Helper.toast(this, error.message.toString(), true)
    }

    var ITEMS : ArrayList<String>? = null
    set(value) {
        field = value
        if (value != null)
            ADAPTER = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, value)
    }

    var ADAPTER : ArrayAdapter<String>? = null
    set(value) {
        field = value
        if(value != null){
            lv_listnames.adapter = value
        }
    }
}