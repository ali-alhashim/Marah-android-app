package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiAddPostComment {
    @FormUrlEncoded
    @POST("api/add/comment")
    fun addPostComment(
        @Field("postId") postId: Int,
        @Field("byUser") byUser: String?,
        @Field("token") token: String?,
        @Field("theComment") theComment: String
    ): Call<postCommentDataClass>


}