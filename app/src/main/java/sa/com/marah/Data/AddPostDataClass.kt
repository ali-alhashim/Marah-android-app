package sa.com.marah.Data

import android.net.Uri

data class AddPostDataClass(
    val id:Int,
    val post_subject:String,
    val token:String,
    val username:String,
    val post_text:String,
    val post_category:String,
    val post_subcategory:String,
    val post_city:String,
    val images : List<Uri>,
    val post_images : List<String>,
    val comments : List<CommentDataClass>,
)
