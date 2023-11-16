package sa.com.marah.Data
import retrofit2.Call
import retrofit2.http.GET
interface ApiServiceCategroies {
    @GET("api/categories")
    fun getCategories(): Call<List<CategroiesDataClass>>
}