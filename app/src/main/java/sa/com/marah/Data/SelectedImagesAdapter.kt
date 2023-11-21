package sa.com.marah.Data
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import sa.com.marah.R

class SelectedImagesAdapter(private val selectedImages: List<SelectedImage>) :
    RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView_selected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedImage = selectedImages[position]
        Glide.with(holder.itemView.context)
            .load(selectedImage.uri)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return selectedImages.size
    }
}