package sa.com.marah.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiPostList {
    @GET("api/posts/list")
    fun getPostList(@Query("page") page: Int,
                    @Query("category") category: Int,
                    @Query("subcategory") subcategory: Int,
                    @Query("location") location: Int
                    ): Call<List<PostCardDataClass>>
}