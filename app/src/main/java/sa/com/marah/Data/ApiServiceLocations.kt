package sa.com.marah.Data

import retrofit2.Call
import retrofit2.http.GET
interface ApiServiceLocations {
    @GET("api/locations")
    fun getLocations(): Call<List<LocationsDataClass>>
}