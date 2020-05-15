package app.kevs.biyang.getlist.libs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.getlist.R
import app.kevs.biyang.getlist.libs.models.GroceryItem
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.getlist_row_item.view.*

class GroceryItemAdapter(val ctx : Context,
                         val items : ArrayList<GroceryItem>,
                         val onRowClick : (item : GroceryItem) -> Unit,
                         val onRowLongClick : (item : GroceryItem) -> Unit)
    :RecyclerView.Adapter<GroceryItemViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        return GroceryItemViewHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.getlist_row_item,
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
            description.text = """
                ${item.quantityType ?: "No Quantity"} ${item.category ?: "No Category"} ${item.remarks ?: "No Remarks"}
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