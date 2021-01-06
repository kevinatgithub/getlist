package app.kevs.biyang.game.libs.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.R
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.models.MonsterCard
import app.kevs.biyang.game.libs.models.Spell
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.getlist_grid_item.view.*

class MonsterCardViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val thumb = view.img_thumb
    val name = view.txt_name
    val description = view.txt_description
    val container = view.cv_item_container
}

enum class AdapterType{
    LIST, GRID
}

class MonsterCardAdapter(val ctx : Context,
                         val items : List<MonsterCard>,
                         val spells : List<Spell>,
                         val orientation : AdapterType? = AdapterType.GRID,
                         val onRowClick : (item : MonsterCard) -> Unit,
                         val onRowLongClick : (item : MonsterCard) -> Unit)
    :RecyclerView.Adapter<MonsterCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonsterCardViewHolder {
        val layout = when (orientation) {
            AdapterType.LIST -> R.layout.getlist_row_item
            AdapterType.GRID -> R.layout.getlist_grid_item
            else -> R.layout.getlist_row_item
        }
        return MonsterCardViewHolder(
            LayoutInflater.from(ctx).inflate(
                layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MonsterCardViewHolder, position: Int) {
        val item = items.get(position)

        holder?.apply {
            name.text = item?.name?.capitalize()
            val len = item?.description?.length ?: 0
            var maxlength = if (len > 85) {
                60
            } else {
                item?.description?.length
            }
            val skill = spells.find { it.name?.toUpperCase().equals(item?.skill?.toUpperCase()) }
            description.text = """
                ${item?.skill ?: "No Skill"}
                ${skill?.description ?: "User Spell"}
            """.trimIndent()

            thumb.setBackgroundDrawable(ctx.resources.getDrawable(R.drawable.a2))

            val imgUrl = item.imgUrl

            async {
                if (!imgUrl.isNullOrEmpty()) {
                    if (imgUrl!!.startsWith("http") && await { Helper.isInternetAvailable() }){
                        val url = imgUrl
                        val bitmap = await { Helper.getBitmapFromUrl(url!!) }
                        holder?.thumb.setImageBitmap(bitmap)
                    }else{
                        holder?.thumb.setImageBitmap(Helper.decodeBase64ToBitmap(imgUrl!!))
                    }
                }else {
                    holder?.thumb.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_info))
                }
            }
            container.setOnClickListener {
                onRowClick(item!!)
            }

            container.setOnLongClickListener {
                onRowLongClick(item!!)
                true
            }
        }
    }
}
