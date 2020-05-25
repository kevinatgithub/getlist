package app.kevs.biyang.getlist.libs.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.getlist.R
import app.kevs.biyang.getlist.libs.Helper
import app.kevs.biyang.getlist.libs.models.MonsterCard
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
            description.text = item?.description?.substring(0, maxlength!!) + " ( " + (item?.skill ?: "No Skill") + " )"

            thumb.setBackgroundDrawable(ctx.resources.getDrawable(R.drawable.a2))

            async {
                if (!item.imgUrl.isNullOrEmpty() && await { Helper.isInternetAvailable() }) {
                    if (item.imgUrl!!.startsWith("http")){
                        val url = item.imgUrl
                        val bitmap = await { Helper.getBitmapFromUrl(url!!) }
                        holder?.thumb.setImageBitmap(bitmap)
                    }else{
                        Helper.fileToImageView(holder?.thumb, item.imgUrl!!)
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
