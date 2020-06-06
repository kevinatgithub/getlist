package app.kevs.biyang.game.libs.api

import app.kevs.biyang.game.libs.models.GroceryItem

object GroceryApiResult {
    data class Result(val data: ArrayList<GroceryItem>)
}