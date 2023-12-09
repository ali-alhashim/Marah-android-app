package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiPostDetail {

    @GET("api/post/detail")
    fun getPost(@Query("postId") postId: Int, @Query("username") username: String?): Call<AddPostDataClass>

}