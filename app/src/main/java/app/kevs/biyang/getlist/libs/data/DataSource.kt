package app.kevs.biyang.getlist.libs.data

import app.kevs.biyang.getlist.libs.models.GroceryItem
import app.kevs.biyang.getlist.libs.models.ItemAlternative
import app.kevs.biyang.getlist.libs.models.MonsterCard
import io.realm.RealmResults

interface DataSource {

    fun getList(onSuccess: (items : ArrayList<GroceryItem>) -> Unit,
                onError : (message : Throwable) -> Unit)

    fun addItem(item : GroceryItem,
                onSuccess: (status : String) -> Unit,
                onError: (message: Throwable) -> Unit)

    fun flagItemAsComplete(item : GroceryItem,
                           onSuccess: (status : String) -> Unit,
                           onError: (message: Throwable) -> Unit)

    fun updateItem(item : GroceryItem,
                           onSuccess: (status : String) -> Unit,
                           onError: (message: Throwable) -> Unit)

    fun deleteItem(item : GroceryItem,
                   onSuccess: (status : String) -> Unit,
                   onError: (message: Throwable) -> Unit)

    fun clearAll(onSuccess: (status : String) -> Unit,
                 onError: (message: Throwable) -> Unit)

    fun syncList(list : ArrayList<GroceryItem>,
                 onSuccess: (status: String) -> Unit,
                 onError: (message: Throwable) -> Unit)

    fun getAlternativeItems(itemId : String?,
                            onResult : (result : ArrayList<ItemAlternative>?) -> Unit,
                            onError: (message: Throwable) -> Unit)

    fun addAlternativeItem(item : ItemAlternative,
                           onSuccess : (status : String) -> Unit,
                           onError: (message: Throwable) -> Unit)

    fun clearAlternativeItems(itemId : String?,
                              onSuccess : (status : String) -> Unit,
                              onError: (message: Throwable) -> Unit)

    fun getItem(itemId: String?,
                onSuccess: (item : GroceryItem?) -> Unit,
                onError: (message: Throwable) -> Unit)

    fun voteAlternative(itemId : String?,
                        item: ItemAlternative,
                        onSuccess: (status: String) -> Unit,
                        onError: (message: Throwable) -> Unit)

    fun deleteAlternative(item: ItemAlternative,
                          onSuccess: (status: String) -> Unit,
                          onError: (message: Throwable) -> Unit)

    fun getListNames(onSuccess: (listNames: ArrayList<String>) -> Unit,
                     onError: (message: Throwable) -> Unit)

    fun getItemsFromList(listName: String,
                         onSuccess: (items: ArrayList<GroceryItem>) -> Unit,
                         onError: (message: Throwable) -> Unit)

    fun assignList(listName: String,
                   onSuccess: (status: String) -> Unit,
                   onError: (message: Throwable) -> Unit)

    fun deleteList(listName: String,
                   onSuccess: (status: String) -> Unit,
                   onError: (message: Throwable) -> Unit)

    fun getCategoriesFromItems(onSuccess: (items: ArrayList<String>) -> Unit,
                               onError: (message: Throwable) -> Unit)

    fun getCards(onResult: (cards : RealmResults<MonsterCard>) -> Unit)

    fun getCard(_id : String, onResult : (card : MonsterCard?) -> Unit)

    fun createCard(card : MonsterCard, onComplete : () -> Unit)

    fun updateCard(card : MonsterCard, onComplete: () -> Unit)

    fun deleteCard(id : String, onComplete: () -> Unit)

    fun clearCards(onComplete: () -> Unit)

    fun transact(onTrasaction: () -> Unit)
}