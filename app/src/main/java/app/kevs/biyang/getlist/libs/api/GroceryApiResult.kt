package app.kevs.biyang.getlist.libs.api

import app.kevs.biyang.getlist.libs.models.GroceryItem

object GroceryApiResult {
    data class Result(val data: ArrayList<GroceryItem>)
}