package sa.com.marah


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.ApiServiceCategroies
import sa.com.marah.Data.CategroiesDataClass


class AddPostFragment : Fragment() {

    private lateinit var postCategorySpinner: Spinner
    private lateinit var options: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

         postCategorySpinner = view.findViewById(R.id.post_category_Spinner)
        //send http get request to api/categories to get the list
        options = mutableListOf()

        val adapterForPostCategory  = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, options)
        adapterForPostCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        postCategorySpinner.adapter = adapterForPostCategory

        //**********************************************
        val api = Retrofit.Builder().baseUrl(MainActivity().BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(
            ApiServiceCategroies::class.java)
        api.getCategories().enqueue(object : Callback<List<CategroiesDataClass>> {
            override fun onResponse(
                call: Call<List<CategroiesDataClass>>,
                response: Response<List<CategroiesDataClass>>
            ) {
                if(response.isSuccessful)
                {
                    Log.d(TAG, "Http get request is Successful for locations")
                    var x = 0
                    response.body()?.let{
                        for (category in it)
                        {
                            Log.i(TAG, "on Response  location Name:${category.name} with Id: ${category.id}")
                            options.add(category.name)
                            x = x + 1
                        }
                    }

                    adapterForPostCategory.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<CategroiesDataClass>>, t: Throwable) {

                Log.d(TAG, "Http get request Failure for locations ${t.message} ............${api.toString()}")
            }

        })
        //***********************************************



        val btn_select_photo:Button = view.findViewById(R.id.btn_select_photo)

        btn_select_photo.setOnClickListener(){
            Log.i(TAG,"popup dialog to select photo")
        }
         return view
    } // end onCreateView










}