package sa.com.marah.Data

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sa.com.marah.MainActivity
import sa.com.marah.R

class PostRecyclerAdapter(private val postList: List<PostCardDataClass>,  private val clickListener: PostClickListener) :
    RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        val postPathTextView:TextView = itemView.findViewById(R.id.postPathTextView)
        val postImage:ImageView = itemView.findViewById(R.id.postImage)
        val postCreatedByTextView:TextView = itemView.findViewById(R.id.postCreatedByTextView)
        val postLocationTextView:TextView = itemView.findViewById(R.id.postLocationTextView)
        val postCreatedDateTextView:TextView = itemView.findViewById(R.id.postCreatedDateTextView)
        // Add references to other UI components for additional post details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.titleTextView.text = currentPost.subject
        holder.postPathTextView.text = "${currentPost.id.toString()}  /  ${currentPost.category__name}  /   ${currentPost.sub_category__name}"
        holder.postCreatedByTextView.text = currentPost.created_by__name
        holder.postLocationTextView.text = currentPost.location__name
        holder.postCreatedDateTextView.text = currentPost.created_date
        val imageUrl = "${MainActivity().BASE_URL}/media/${currentPost.first_image}"
        Log.i(TAG, "the Image data = $imageUrl")
        Glide.with(holder.itemView).load(imageUrl).into(holder.postImage)
        // Set other UI components with post details
        clickListener.onPostClick(currentPost)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
