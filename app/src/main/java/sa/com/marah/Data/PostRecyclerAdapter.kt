package sa.com.marah.Data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.com.marah.R

class PostRecyclerAdapter(private val postList: List<PostCardDataClass>) :
    RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        // Add references to other UI components for additional post details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.titleTextView.text = currentPost.subject
        // Set other UI components with post details
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
