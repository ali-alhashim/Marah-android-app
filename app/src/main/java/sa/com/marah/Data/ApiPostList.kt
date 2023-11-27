package sa.com.marah.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiPostList {
    @GET("api/posts/list")
    fun getPostList(@Query("page") page: Int,
                    @Query("category") category: String,
                    @Query("subcategory") subcategory: String,
                    @Query("location") location: String
                    ): Call<List<PostCardDataClass>>
}