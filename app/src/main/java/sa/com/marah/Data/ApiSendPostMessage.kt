package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiSendPostMessage {

    @FormUrlEncoded
    @POST("api/send/message")
    fun sendPostMessage(

        @Field("from_user") from_user: String?,
        @Field("token") token: String?,
        @Field("send_to") send_to: String,
        @Field("message") message: String,
        @Field("subject") subject: String,
    ): Call<postCommentDataClass>
}