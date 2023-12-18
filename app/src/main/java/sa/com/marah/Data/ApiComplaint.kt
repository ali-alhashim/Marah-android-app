package sa.com.marah.Data


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

import retrofit2.http.POST


interface ApiComplaint {
    @FormUrlEncoded
    @POST("api/send/complaint")
    fun AddComplaint(
        @Field("username") username: String?,
        @Field("subject") subject: String,
        @Field("text") text: String,
        @Field("postId") postId:Int

        ): Call<complaintData>
}