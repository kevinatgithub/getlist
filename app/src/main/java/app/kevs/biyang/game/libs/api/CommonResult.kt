package app.kevs.biyang.game.libs.api

import app.kevs.biyang.game.libs.models.GroceryItem
import app.kevs.biyang.game.libs.models.ItemAlternative
import app.kevs.biyang.game.libs.models.MonsterCard

object CommonResult {
    data class Result(val data: String)
    data class AlternativeItemQueryResult(val items: ArrayList<ItemAlternative>)
    data class ItemResult(val item : GroceryItem)
    data class ListNames(val listNames : ArrayList<String>)
    data class Enkanto(val state: State)
}

class State(
    var cards: List<MonsterCard>,
    var ais: List<MonsterCard>,
    var spells: List<MonsterCard>
)