package app.kevs.biyang.getlist.libs.api

import app.kevs.biyang.getlist.libs.models.GroceryItem
import app.kevs.biyang.getlist.libs.models.ItemAlternative

object CommonResult {
    data class Result(val data: String)
    data class AlternativeItemQueryResult(val items: ArrayList<ItemAlternative>)
    data class ItemResult(val item : GroceryItem)
    data class ListNames(val listNames : ArrayList<String>)
}