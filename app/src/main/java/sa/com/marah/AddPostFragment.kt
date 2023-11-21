package sa.com.marah



import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import kotlin.math.min


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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.ApiServiceCategroies
import sa.com.marah.Data.ApiServiceLocations
import sa.com.marah.Data.ApiServiceSubCategories
import sa.com.marah.Data.CategroiesDataClass
import sa.com.marah.Data.LocationsDataClass
import sa.com.marah.Data.SelectedImage
import sa.com.marah.Data.SelectedImagesAdapter
import sa.com.marah.Data.SubCategoryDataClass


class AddPostFragment : Fragment() {

    private val PICK_IMAGES_REQUEST_CODE = 1001

    private val selectedImagesList: MutableList<SelectedImage> = mutableListOf()
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter

    private lateinit var postCategorySpinner: Spinner
    private lateinit var postSubCategorySpinner: Spinner
    private lateinit var postCitySpinner : Spinner
    private lateinit var optionsSubCategories : MutableList<String>
    private lateinit var optionsCity : MutableList<String>
    private lateinit var options: MutableList<String>

    //--


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

         postCategorySpinner    = view.findViewById(R.id.post_category_Spinner)
         postSubCategorySpinner = view.findViewById(R.id.post_subcategory_Spinner)
         postCitySpinner        = view.findViewById(R.id.post_city_Spinner)

        val recyclerView_selectedImages :RecyclerView = view.findViewById(R.id.recyclerView_selectedImages)

        //send http get request to api/categories to get the list
        options = mutableListOf()
        optionsCity = mutableListOf()

        //-----City Adapter
        val adapterForCities = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, optionsCity)
        adapterForCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        postCitySpinner.adapter = adapterForCities
        //-----/

        //************* call location api
        val apiCity = Retrofit.Builder().baseUrl(MainActivity().BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(
            ApiServiceLocations::class.java)
        apiCity.getLocations().enqueue(object : Callback<List<LocationsDataClass>> {
            override fun onResponse(
                call: Call<List<LocationsDataClass>>,
                response: Response<List<LocationsDataClass>>
            ) {
                if(response.isSuccessful)
                {
                    Log.d(TAG, "Http get request is Successful for locations")
                    var x = 0
                    response.body()?.let{
                        for (city in it)
                        {
                            //Log.i(TAG, "on Response  location Name:${category.name} with Id: ${category.id}")
                            optionsCity.add(city.name)
                            x = x + 1
                        }
                    }

                    adapterForCities.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<LocationsDataClass>>, t: Throwable) {

                Log.d(TAG, "Http get request Failure for locations ${t.message} ............${apiCity.toString()}")
            }

        })
        //*************/ locations

        //----- adapter for category
        val adapterForPostCategory  = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, options)
        adapterForPostCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        postCategorySpinner.adapter = adapterForPostCategory
        //------/


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

        // Select photo or use Camera ----------------
        btn_select_photo.setOnClickListener(){
            Log.i(TAG,"popup dialog to select photo")

            pickImages()

        }
        //---------------------------------------------



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



        // Initialize RecyclerView and its adapter
        val recyclerViewSelectedImages: RecyclerView = view.findViewById(R.id.recyclerView_selectedImages)
        recyclerViewSelectedImages.layoutManager = GridLayoutManager(requireContext(), 3)
        selectedImagesAdapter = SelectedImagesAdapter(selectedImagesList)
        recyclerViewSelectedImages.adapter = selectedImagesAdapter




         return view
    } // end onCreateView------------------------------------------------------------------------------------------------------------------

    //************************** image selection -----------------
    private fun pickImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"

        startActivityForResult(intent, PICK_IMAGES_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImages: ArrayList<Uri> = ArrayList()

            if (data?.clipData != null) {
                // Multiple images selected
                val clipData = data.clipData
                if (clipData != null) {
                    for (i in 0 until min(clipData.itemCount, 10)) {
                        val uri = clipData.getItemAt(i).uri
                        selectedImages.add(uri)
                        Log.d(TAG, "Selected Image: $uri")
                    }
                }
            } else if (data?.data != null) {
                // Single image selected
                val uri = data.data
                if (uri != null) {
                    selectedImages.add(uri)
                    Log.d(TAG, "Selected Image: $uri")
                }
            }

            // Clear the existing list before adding new images
            selectedImagesList.clear()

            // Add selected images to the list (up to a maximum of 10)
            for (uri in selectedImages) {
                if (selectedImagesList.size < 10) {
                    selectedImagesList.add(SelectedImage(uri))
                } else {
                    // Handle the case where the maximum limit is reached
                    // You might want to show a message to the user or take other actions
                    Log.d(TAG, "Maximum limit of 10 images reached.")
                    break
                }
            }

            // Update the RecyclerView
            selectedImagesAdapter.notifyDataSetChanged()
        }
    }



    companion object {
        private const val TAG = "AddPostFragment"
    }

    //**************************************************************





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