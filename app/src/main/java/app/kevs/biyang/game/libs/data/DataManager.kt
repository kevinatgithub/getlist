package app.kevs.biyang.game.libs.data

import android.content.Context
import app.kevs.biyang.game.MODE
import app.kevs.biyang.game.MainActivity
import app.kevs.biyang.game.libs.api.State
import app.kevs.biyang.game.libs.data.source.Api
import app.kevs.biyang.game.libs.data.source.Db
import app.kevs.biyang.game.libs.models.Category
import app.kevs.biyang.game.libs.models.GroceryItem
import app.kevs.biyang.game.libs.models.ItemAlternative
import app.kevs.biyang.game.libs.models.MonsterCard
import io.realm.Realm
import io.realm.RealmResults

enum class SOURCE_TYPE{
    API, DATABASE
}

class DataManager(context: Context) : DataSource {
    val SOURCE = SOURCE_TYPE.API
    val api = Api()
    val db = Db(context)

    private fun getDataSource() : DataSource{
        if (MainActivity.APP_MODE == MODE.ONLINE){
            return api
        }
        return db
    }

    override fun getList(
        onSuccess: (items: ArrayList<GroceryItem>) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getList(onSuccess, onError)
    }

    override fun addItem(
        item: GroceryItem,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().addItem(item, onSuccess, onError)
    }

    override fun flagItemAsComplete(
        item: GroceryItem,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().flagItemAsComplete(item,onSuccess,onError)
    }

    override fun updateItem(
        item: GroceryItem,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().updateItem(item, onSuccess, onError)
    }

    override fun deleteItem(
        item: GroceryItem,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().deleteItem(item, onSuccess, onError)
    }

    override fun clearAll(
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().clearAll(onSuccess, onError)
    }

    override fun syncList(
        list: ArrayList<GroceryItem>,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        when(MainActivity.APP_MODE){
            MODE.ONLINE -> db.syncList(list, onSuccess, onError)
            MODE.OFFLINE -> api.syncList(list, onSuccess, onError)
        }
    }

    override fun getAlternativeItems(
        itemId: String?,
        onResult: (result: ArrayList<ItemAlternative>?) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getAlternativeItems(itemId, onResult, onError)
    }

    override fun addAlternativeItem(
        item: ItemAlternative,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().addAlternativeItem(item,onSuccess,onError)
    }

    override fun clearAlternativeItems(
        itemId: String?,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().clearAlternativeItems(itemId, onSuccess, onError)
    }

    override fun getItem(
        itemId: String?,
        onSuccess: (item: GroceryItem?) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getItem(itemId, onSuccess, onError)
    }

    override fun voteAlternative(
        itemId: String?,
        item: ItemAlternative,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().voteAlternative(itemId, item, onSuccess, onError)
    }

    override fun deleteAlternative(
        item: ItemAlternative,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().deleteAlternative(item, onSuccess, onError)
    }

    override fun assignList(
        listName: String,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().assignList(listName, onSuccess, onError)
    }

    override fun deleteList(
        listName: String,
        onSuccess: (status: String) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().deleteList(listName, onSuccess, onError)
    }

    override fun getCategoriesFromItems(
        onSuccess: (items: ArrayList<String>) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getCategoriesFromItems(onSuccess, onError)
    }

    override fun getCards(onResult: (cards: RealmResults<MonsterCard>) -> Unit) {
        getDataSource().getCards(onResult)
    }

    override fun getCard(_id: String, onResult: (card: MonsterCard?) -> Unit) {
        getDataSource().getCard(_id, onResult)
    }

    override fun createCard(card: MonsterCard, onComplete: () -> Unit) {
        getDataSource().createCard(card, onComplete)
    }

    override fun updateCard(card: MonsterCard, onComplete: () -> Unit) {
        getDataSource().updateCard(card, onComplete)
    }

    override fun deleteCard(id: String, onComplete: () -> Unit) {
        getDataSource().deleteCard(id, onComplete)
    }

    override fun clearCards(onComplete: () -> Unit) {
        getDataSource().clearCards(onComplete)
    }

    override fun transact(onTrasaction: (realm : Realm) -> Unit) {
        getDataSource().transact(onTrasaction)
    }

    override fun uploadData(
        cards: String?,
        ais: String?,
        spells: String?,
        onResult: () -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        api.uploadData(cards, ais, spells, onResult, onError)
    }

    override fun downloadData(
        onResult: (state: State) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        api.downloadData(onResult, onError)
    }

    override fun getListNames(
        onSuccess: (listNames: ArrayList<String>) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getListNames(onSuccess, onError)
    }

    override fun getItemsFromList(
        listName: String,
        onSuccess: (items: ArrayList<GroceryItem>) -> Unit,
        onError: (message: Throwable) -> Unit
    ) {
        getDataSource().getItemsFromList(listName, onSuccess, onError)
    }

    fun dispose(){
        if (MainActivity.APP_MODE == MODE.ONLINE)
            api.dispose()
    }

    fun getCategories(callback : (categories : ArrayList<Category>) -> Unit){
        db.getCategories(callback)
    }

    fun addCategory(category : String, callback : (status : String) -> Unit){
        db.addCategory(category, callback)
    }

    fun deleteCategory(category : String, callback : (status : String) -> Unit){
        db.deleteCategory(category, callback)
    }

    fun clearCategories(callback : (status : String) -> Unit){
        db.clearCategories(callback)
    }

    fun setList(list: ArrayList<GroceryItem>, onSuccess: (status : String) -> Unit, onError: (error : Throwable) -> Unit) {
        getDataSource().syncList(list, onSuccess, onError)
    }
}