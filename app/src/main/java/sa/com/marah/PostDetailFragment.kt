package sa.com.marah

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.AddPostDataClass
import sa.com.marah.Data.ApiAddPostComment
import sa.com.marah.Data.ApiPostDetail
import sa.com.marah.Data.ApiPostList
import sa.com.marah.Data.ApiServiceLogin
import sa.com.marah.Data.LoginDataClass
import sa.com.marah.Data.PostCardDataClass
import sa.com.marah.Data.postCommentDataClass
import java.net.URL


class PostDetailFragment(postId: Int) : Fragment() {
    val postId = postId
    private lateinit var createdBy: TextView
    private lateinit var postText:TextView
    private lateinit var postSubject:TextView
    private lateinit var postPath:TextView
    private lateinit var postLayout:LinearLayout
    private lateinit var d_post_root_layout:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)
        Log.i(TAG, "this Fragment for post detail Id = ${postId}")

        createdBy   = view.findViewById(R.id.d_post_createdby)
        postText    = view.findViewById(R.id.d_post_text)
        postSubject = view.findViewById(R.id.d_post_subject)
        postPath    = view.findViewById(R.id.d_post_path)
        postLayout  = view.findViewById(R.id.d_post_layout)
        d_post_root_layout = view.findViewById(R.id.d_post_root_layout)

        loadPostDetail(postId)
        return view
    }


    fun loadPostDetail(postId: Int)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity().BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiPostDetail::class.java)
        apiService.getPost(postId).enqueue(object :
            Callback<AddPostDataClass> {
            override fun onResponse(
                call: Call<AddPostDataClass>,
                response: Response<AddPostDataClass>
            ) {
                Log.i(TAG,"onResponse =>")
                if(response.isSuccessful)
                {

                    Log.i(TAG,"onResponse => is Successful")
                    val post: AddPostDataClass? = response.body()

                    if (post != null)
                    {
                      Log.i(TAG, "we get the post detail ${post.id} \n ${post.post_subject} \n ${post.username} \n ${post.post_text} \n ${post.post_images}")

                        postSubject.text = post.post_subject
                        createdBy.text = post.username
                        postText.text = post.post_text
                        postPath.text = "${post.id} / ${post.post_category} / ${post.post_subcategory}"
                        val requestOptions = RequestOptions().centerCrop()
                        for(image in post.post_images)
                        {
                            val imageView = ImageView(requireContext())

                            Glide.with(requireContext())
                                .load("${MainActivity().BASE_URL}${image.removePrefix("/")}")
                                .apply(requestOptions)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imageView)

                            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                            layoutParams.setMargins(5, 5,5, 5)
                            imageView.setPadding(1,1,1,1)
                            imageView.layoutParams = layoutParams
                            imageView.adjustViewBounds = true







                            postLayout.addView(imageView)
                        }

                        // add post comment

                        for(comment in post.comments)
                        {
                            Log.i(TAG,"post comment: By ${comment.comment_user} , ${comment.comment_text}")
                            val commentLayout:LinearLayout = LinearLayout(requireContext())
                            commentLayout.setBackgroundResource(R.drawable.rounded_card_shape)
                            val commentLayoutParams = LinearLayout.LayoutParams(
                                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                                                                 )
                            commentLayoutParams.layoutDirection = LinearLayout.VERTICAL

                            commentLayoutParams.setMargins(5,10,5,10)

                            commentLayout.layoutParams = commentLayoutParams
                            commentLayout.setPadding(5,5,5,5)

                            val TextCommentBy:TextView = TextView(requireContext())
                            val textCommentByParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            textCommentByParams.setMargins(10, 10, 10, 10)
                            TextCommentBy.layoutParams = textCommentByParams
                            TextCommentBy.setTextColor(Color.BLACK)
                            TextCommentBy.setPadding(5,5,5,5)

                            TextCommentBy.text = comment.comment_user

                            val commentDate:TextView = TextView(requireContext())
                            commentDate.text = comment.created_date
                            commentDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,9f)
                            commentDate.setTextColor(Color.BLACK)
                            val commentText:TextView = TextView(requireContext())
                            commentText.setTextColor(Color.BLACK)
                            commentText.text = comment.comment_text
                            commentText.layoutParams = textCommentByParams
                            commentText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
                            // Set orientation to vertical for the commentLayout
                            commentLayout.orientation = LinearLayout.VERTICAL
                            commentLayout.addView(TextCommentBy)
                            commentLayout.addView(commentDate)
                            commentLayout.addView(commentText)
                            d_post_root_layout.addView(commentLayout)



                        }

                        // add TextEdit for adding user comment
                        val AddingCommentLayout:LinearLayout = LinearLayout(requireContext())
                        AddingCommentLayout.setBackgroundResource(R.drawable.rounded_card_shape)
                        val AddingcommentLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        AddingCommentLayout.layoutParams = AddingcommentLayoutParams
                        AddingCommentLayout.orientation = LinearLayout.VERTICAL
                        AddingCommentLayout.setPadding(10,10,10,10)

                        val theComment = EditText(requireContext())
                        theComment.setTextColor(Color.BLACK)
                        val theCommentParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            200
                        )
                        theComment.hint = "أكتب رساله هنا"

                        theComment.setHintTextColor(Color.DKGRAY)
                        theCommentParams.setMargins(10,10,10,10)
                        theComment.layoutParams = theCommentParams
                        theComment.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
                        theComment.setPadding(5,5,5,5)
                        theComment.setBackgroundResource(R.drawable.rounded_shap_white)
                        AddingCommentLayout.addView(theComment)

                        val sendBtn = Button(requireContext())
                        sendBtn.setTextColor(Color.BLACK)

                        sendBtn.setBackgroundResource(R.drawable.rounded_shape)
                        sendBtn.text = "إرسال"
                        sendBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        AddingCommentLayout.addView(sendBtn)

                        d_post_root_layout.addView(AddingCommentLayout)


                        sendBtn.setOnClickListener()
                        {
                            // send comment to api/add/NewComment
                            // comment value, by user with his token
                            val mainActivity = activity as? MainActivity
                            Log.i(TAG, "you want to add the following comment ${theComment.text} \n" +
                                    "by ${mainActivity?.getCurrentUser()} \n" +
                                    "with token ${mainActivity?.getCurrentToken()} for postId ${post.id}")
                            sendCommentPost(post.id, mainActivity?.getCurrentUser(), mainActivity?.getCurrentToken(), theComment.text.toString())
                        }


                    }
                    else{
                        Log.e(TAG, "post = null !")
                    }

                }
            }

            override fun onFailure(call: Call<AddPostDataClass>, t: Throwable) {
                Log.e(TAG,"Failure ${t.message}")
            }
        })
    }



   // fun convertToUrlList(imagePaths: List<String>): List<URL> {
     //   return imagePaths.map { URL(it) }
   // }

    fun sendCommentPost(postId:Int, byUser:String?, token:String?, theComment:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity().BASE_URL)  // Replace with your API's base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiAddPostComment::class.java)
        val call = apiService.addPostComment(postId, byUser, token, theComment)

        call.enqueue(object : Callback<postCommentDataClass>
        {
            override fun onResponse(
                call: Call<postCommentDataClass>,
                response: Response<postCommentDataClass>
            ) {
                if (response.isSuccessful)
                {
                    response.body()?.let { Log.i(TAG, it.status) }
                    (requireActivity() as MainActivity).openFragment(PostDetailFragment(postId))
                }
            }

            override fun onFailure(call: Call<postCommentDataClass>, t: Throwable) {
               Log.e(TAG, t.message.toString())
            }
        })
    }


}