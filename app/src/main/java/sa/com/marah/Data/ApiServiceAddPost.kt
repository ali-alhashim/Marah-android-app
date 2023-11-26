package sa.com.marah.Data


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call


import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServiceAddPost{
    @Multipart
    @POST("api/addpost")
    fun AddPost(
        @Part("post_subject") postSubject: RequestBody,
        @Part("post_text") postText: RequestBody,
        @Part("post_category") postCategory: RequestBody,
        @Part("post_subcategory") postSubcategory: RequestBody,
        @Part("post_city") postCity: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part("username") username: RequestBody?,
        @Part("token") token: RequestBody?
    ): Call<AddPostDataClass>
}
