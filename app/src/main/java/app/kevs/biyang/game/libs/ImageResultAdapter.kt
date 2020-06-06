package app.kevs.biyang.game.libs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.R
import app.kevs.biyang.game.libs.api.SearchImageResult

class ImageResultAdapter(val context : Context,
                         val imageResults : ArrayList<SearchImageResult.ImageResult>,
                         val onClick : (img : SearchImageResult.ImageResult) -> Unit) : RecyclerView.Adapter<ImageViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        TODO("Replace Layout file")
        return ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.getlist_row_item, parent, false))
    }

    override fun getItemCount(): Int {
        return imageResults.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        /*val imgResult = imageResults.get((position))

        if (imgResult.resizedBitmap != null){
            holder?.img.setImageBitmap(imgResult.resizedBitmap)
            holder?.img_container.setOnClickListener {
                onClick(imgResult)
            }
        }else{
            holder?.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_info))
        }*/
    }

}

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){
/*    val img = view.img
    val img_container = view.img_container*/
}