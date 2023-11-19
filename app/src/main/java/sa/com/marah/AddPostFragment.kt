package sa.com.marah


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.ApiServiceCategroies
import sa.com.marah.Data.ApiServiceSubCategories
import sa.com.marah.Data.CategroiesDataClass
import sa.com.marah.Data.SubCategoryDataClass


class AddPostFragment : Fragment() {

    private lateinit var postCategorySpinner: Spinner
    private lateinit var postSubCategorySpinner: Spinner
    private lateinit var optionsSubCategories : MutableList<String>
    private lateinit var options: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

         postCategorySpinner = view.findViewById(R.id.post_category_Spinner)
        postSubCategorySpinner = view.findViewById(R.id.post_subcategory_Spinner)
        //send http get request to api/categories to get the list
        options = mutableListOf()

        val adapterForPostCategory  = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, options)
        adapterForPostCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        postCategorySpinner.adapter = adapterForPostCategory


        // onSelected item event
        postCategorySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = options[position]
                // Perform actions based on the selected category
                Log.i(TAG, "Selected Category: $selectedCategory")
                loadSubCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        })
        //----------------------

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
                            //Log.i(TAG, "on Response  location Name:${category.name} with Id: ${category.id}")
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



         if (postCategorySpinner.count > 0)
        {
            val firstCategory = postCategorySpinner.getItemAtPosition(0) as SubCategoryDataClass
            val firstCategoryName = firstCategory.name
            Log.i(TAG,"the First Item in Category is $firstCategoryName")
            loadSubCategory(firstCategoryName)
        }
         else
         {
             Log.i(TAG,"postCategorySpinner.count < 0 !")
         }





         return view
    } // end onCreateView




    fun loadSubCategory(category: String)
    {
        Log.i(TAG, " you want to get sub category for $category")
        optionsSubCategories = mutableListOf()

        val adapterForPostSubCategory  = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, optionsSubCategories)
        adapterForPostSubCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        postSubCategorySpinner.adapter = adapterForPostSubCategory

      // http get request with parameter ?category=category
        val api = Retrofit.Builder()
            .baseUrl(MainActivity().BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceSubCategories::class.java)
        //----
        api.getSubCategories(category).enqueue(object : Callback<List<SubCategoryDataClass>> {
            override fun onResponse(
                call: Call<List<SubCategoryDataClass>>,
                response: Response<List<SubCategoryDataClass>>
            ) {
               //----
                if(response.isSuccessful)
                {
                    Log.d(TAG, "Http get request is Successful for sub Category")
                    var x = 0
                    response.body()?.let{
                        for (subcategory in it)
                        {
                            Log.i(TAG, "on Response  subcategory Name:${subcategory.name} with Id: ${subcategory.id}")
                            optionsSubCategories.add(subcategory.name)
                            x = x + 1
                        }
                    }

                    adapterForPostSubCategory.notifyDataSetChanged()
                }
                //----
            }

            override fun onFailure(call: Call<List<SubCategoryDataClass>>, t: Throwable) {
                Log.d(TAG, "Http get request Failure for sub-categories ${t.message}")
            }


        })
        //----
    }






}