package app.kevs.biyang.game.libs.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.R
import app.kevs.biyang.game.libs.Helper
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.item_hotel_page_travelum.view.*

class ImageGridViewHolder(view : View) : RecyclerView.ViewHolder(view){
    var image = view.image2
}

class ImageGridAdapter(val ctx : Context, val items : List<String>, val onImageClick : (bmp : Bitmap) -> Unit)
    : RecyclerView.Adapter<ImageGridViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGridViewHolder {
        return ImageGridViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_hotel_page_travelum, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ImageGridViewHolder, position: Int) {
        val url = items.get(position)
        holder.apply {
            async {
                val bmp = await { Helper.getBitmapFromUrl(url) }
                image.setImageBitmap(bmp)
                image.setOnClickListener {
                    onImageClick(bmp!!)
                }
            }
        }
    }
}