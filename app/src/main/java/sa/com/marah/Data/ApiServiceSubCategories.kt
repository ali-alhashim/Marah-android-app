package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceSubCategories {
    @GET("api/subCategory")
    fun getSubCategories(@Query("category") category: String): Call<List<SubCategoryDataClass>>
}