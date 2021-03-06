package app.kevs.biyang.game.libs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.R
import app.kevs.biyang.game.libs.models.GroceryItem
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.getlist_row_item.view.*

enum class AdapterOrientation{
    VERTICAL, HORIZONTAL
}

class GroceryItemAdapter(val ctx : Context,
                         val items : ArrayList<GroceryItem>,
                         val orientation : AdapterOrientation? = AdapterOrientation.VERTICAL,
                         val onRowClick : (item : GroceryItem) -> Unit,
                         val onRowLongClick : (item : GroceryItem) -> Unit)
    :RecyclerView.Adapter<GroceryItemViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        val layout = when (orientation){
            AdapterOrientation.VERTICAL -> R.layout.getlist_row_item
            AdapterOrientation.HORIZONTAL -> R.layout.getlist_grid_item
            else -> R.layout.getlist_row_item
        }
        return GroceryItemViewHolder(
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

    override fun onBindViewHolder(holder: GroceryItemViewHolder, position: Int) {
        val item = items.get(position)

        holder?.apply {
            val completeStatus = if (item.isComplete != null){
                if (item.isComplete!!){
                    "Completed"
                }else{
                    ""
                }
            }else{
                ""
            }
            name.text = item.name?.capitalize()
            val len = item.remarks?.length ?: 0
            var maxlength = if (len > 85){
                85
            }else{
                item.remarks?.length
            }
            description.text = """
                ${item.quantityType ?: ""} ${item.category ?: ""} ${item.remarks?.substring(0,maxlength!!) ?: ""}
                ${completeStatus}
            """.trimIndent()

            thumb.setBackgroundDrawable(ctx.resources.getDrawable(R.drawable.a2))

            if (!item.imgUrl.isNullOrEmpty()){
                val url = item.imgUrl
                async {
                    val bitmap = await { Helper.getBitmapFromUrl(url!!) }
                    holder?.thumb.setImageBitmap(bitmap)
                }
            }else {
                holder?.thumb.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_info))
            }
            container.setOnClickListener {
                onRowClick(item)
            }

            container.setOnLongClickListener {
                onRowLongClick(item)
                true
            }
        }
    }
}

class GroceryItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val thumb = view.img_thumb
    val name = view.txt_name
    val description = view.txt_description
    val container = view.cv_item_container
}