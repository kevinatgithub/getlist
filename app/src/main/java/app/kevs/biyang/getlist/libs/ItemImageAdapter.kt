package app.kevs.biyang.getlist.libs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.getlist.R
import kotlinx.android.synthetic.main.item_hotel_page_travelum.view.*

class ItemImageAdapter(val ctx: Context, val images : ArrayList<ItemImage>, val onClick : (i : Int) -> Unit)
    : RecyclerView.Adapter<ItemImageViewHolder>() {
    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImageViewHolder {
        return ItemImageViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_hotel_page_travelum, parent, false))
    }

    override fun onBindViewHolder(holder: ItemImageViewHolder, position: Int) {
        val item = images.get(position)
        holder?.apply {
            var bmp = if (item.isActive){
                Helper.addWatermarkToBitmap(item.imgBmp,
                    "SELECTED",
                    Point(100,100), ctx.resources.getColor(R.color.white) , 500, 30f, false)
            }else{
                item.imgBmp
            }

            image.setImageBitmap(bmp)
            container.setOnClickListener {
                onClick(position)
            }
        }
    }
}

class ItemImageViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val image = view.image2
    val container = view.image_container
}

open class ItemImage (var imgUrl : String, var imgBmp : Bitmap, var isActive : Boolean)
