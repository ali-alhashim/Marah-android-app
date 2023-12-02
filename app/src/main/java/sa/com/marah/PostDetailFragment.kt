package sa.com.marah

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import sa.com.marah.Data.ApiPostDetail
import sa.com.marah.Data.ApiPostList
import sa.com.marah.Data.PostCardDataClass
import java.net.URL


class PostDetailFragment(postId: Int) : Fragment() {
    val postId = postId
    private lateinit var createdBy: TextView
    private lateinit var postText:TextView
    private lateinit var postSubject:TextView
    private lateinit var postPath:TextView
    private lateinit var postLayout:LinearLayout
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


                            val layoutParams =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                            layoutParams.setMargins(0, 1,0, 1)
                            imageView.setPadding(1,1,1,1)
                            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            imageView.layoutParams = layoutParams



                            postLayout.addView(imageView)
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



    fun convertToUrlList(imagePaths: List<String>): List<URL> {
        return imagePaths.map { URL(it) }
    }


}