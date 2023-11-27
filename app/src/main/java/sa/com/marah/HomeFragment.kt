package sa.com.marah

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.ApiPostList
import sa.com.marah.Data.ApiServiceAddPost
import sa.com.marah.Data.PostCardDataClass
import sa.com.marah.Data.PostRecyclerAdapter
import sa.com.marah.Data.SubCategoryDataClass

class HomeFragment : Fragment() {
    private lateinit var PostRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(ContentValues.TAG, "HomeFragment onCreateView")
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        PostRecyclerView = view.findViewById(R.id.PostsRecyclerView)
        loadPostList()

        return view
    } // end of on createView


    fun loadPostList(page:Int=1,category:String="all", subcategory:String="all", location:String="all" )
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity().BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiPostList::class.java)
        apiService.getPostList(page,category, subcategory, location).enqueue(object :
            Callback<List<PostCardDataClass>> {
            override fun onResponse(
                call: Call<List<PostCardDataClass>>,
                response: Response<List<PostCardDataClass>>
            ) {
                Log.i(TAG,"onResponse =>")
                if(response.isSuccessful)
                {

                    Log.i(TAG,"onResponse => is Successful")
                    val postList: List<PostCardDataClass>? = response.body()

                    if (postList != null)
                    {
                        Log.i(TAG, "onResponse => is Successful. Post list size: ${postList.size}")
                        updateRecyclerView(postList)
                    }

                }
            }

            override fun onFailure(call: Call<List<PostCardDataClass>>, t: Throwable) {
                Log.e(TAG,"Failure ${t.message}")
            }
        })
    } // close load post list function


    private fun updateRecyclerView(postList: List<PostCardDataClass>) {
        // Assuming you have a RecyclerView.Adapter
        val adapter = PostRecyclerAdapter(postList)
        PostRecyclerView.adapter = adapter
        // You might also need to set a LayoutManager if not already set
        // Ensure UI operations are on the main thread
        activity?.runOnUiThread {
            // Set a LinearLayoutManager
            PostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }




}