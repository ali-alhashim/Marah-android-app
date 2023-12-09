package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiAddToMyFavorite {
    @FormUrlEncoded
    @POST("api/add/favorite")
    fun addToMyFavorite
                (
                    @Field("postId") postId: Int,
                    @Field("username") username: String?,
                    @Field("token") token: String?,

                    ): Call<AddFavoriteDataClass>

}