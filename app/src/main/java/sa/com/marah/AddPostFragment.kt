package sa.com.marah



import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context

import android.content.Intent
import android.net.Uri
import kotlin.math.min
import okhttp3.MediaType.Companion.toMediaTypeOrNull


import android.os.Bundle
import android.os.Environment


import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar

import android.widget.Spinner
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.AddPostDataClass
import sa.com.marah.Data.ApiServiceAddPost
import sa.com.marah.Data.ApiServiceCategroies
import sa.com.marah.Data.ApiServiceLocations
import sa.com.marah.Data.ApiServiceSubCategories
import sa.com.marah.Data.CategroiesDataClass
import sa.com.marah.Data.LocationsDataClass
import sa.com.marah.Data.SelectedImage
import sa.com.marah.Data.SelectedImagesAdapter
import sa.com.marah.Data.SubCategoryDataClass
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class AddPostFragment : Fragment() {

    private val PICK_IMAGES_REQUEST_CODE = 1001

    private val selectedImagesList: MutableList<SelectedImage> = mutableListOf() // images input
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter
    private lateinit var postSubjectInput:TextInputEditText // post subject input
    private lateinit var postText: TextInputEditText // post text input
    private lateinit var postCategorySpinner: Spinner    //Category input
    private lateinit var postSubCategorySpinner: Spinner // sub Category input
    private lateinit var postCitySpinner : Spinner       // City input
    private lateinit var optionsSubCategories : MutableList<String>
    private lateinit var optionsCity : MutableList<String>
    private lateinit var options: MutableList<String>

    //--


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

         postCategorySpinner    = view.findViewById(R.id.post_category_Spinner)
         postSubCategorySpinner = view.findViewById(R.id.post_subcategory_Spinner)
         postCitySpinner        = view.findViewById(R.id.post_city_Spinner)
        postSubjectInput        = view.findViewById(R.id.post_subject)
        postText                = view.findViewById(R.id.post_text)
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



        val submitBtn : Button = view.findViewById(R.id.btn_post_submit)
        submitBtn.setOnClickListener()
        {
            val progressBar: ProgressBar = view.findViewById(R.id.progressBar1)
            progressBar.visibility = View.VISIBLE
            // get all inputs and send to api/addpost
            Log.i(TAG, "submit button clicked --------------------------------------")


            // we have all input now submit http post request and go to post detail
            // before you send post request check in token with user is there so we can post
            // if not show message that you must login to send post
            //---------------------------***********************
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(ApiServiceAddPost::class.java)
            // Prepare textual data
            val postSubject     = postSubjectInput.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val postText        = postText.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val postCategory    = postCategorySpinner.selectedItem.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val postSubcategory = postSubCategorySpinner.selectedItem.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val postCity        = postCitySpinner.selectedItem.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val mainActivity = activity as? MainActivity
            val usernameD = mainActivity?.getCurrentUser()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val tokenD    = mainActivity?.getCurrentToken()?.toRequestBody("text/plain".toMediaTypeOrNull())

            Log.i(TAG,"post subject = ${postSubjectInput.text} \n Category = ${postCategorySpinner.selectedItem} " +
                    "\n sub Category = ${postSubCategorySpinner.selectedItem} \n Citiy = ${postCitySpinner.selectedItem}" +
                    "\n post text = ${postText} \n selected Images List = ${selectedImagesList.size}"+
                    "\n ${usernameD} \n ${tokenD}"
            )

            // Prepare image data
            // Prepare image data
            val imageUris = selectedImagesList.map { it.uri }
            val imageParts = imageUris.mapNotNull { uri ->
                val file = getFileFromUri(uri)

                if (file != null && file.exists()) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images", file.name, requestFile)
                } else {
                    Log.e(TAG, "File does not exist for URI: $uri")
                    null
                }
            }


// Make the API call
            val call: Call<AddPostDataClass> = apiService.AddPost(
                postSubject,
                postText,
                postCategory,
                postSubcategory,
                postCity,
                imageParts,
                usernameD,
                tokenD

            )

            call.enqueue(object : Callback<AddPostDataClass> {
                override fun onResponse(call: Call<AddPostDataClass>, response: Response<AddPostDataClass>) {
                    if (response.isSuccessful) {
                        // Handle the successful response here
                        val responseData = response.body()
                        Log.i(TAG,responseData.toString())
                        // Do something with the responseData
                        progressBar.visibility = View.GONE
                        (requireActivity() as MainActivity).openFragment(HomeFragment())
                    } else {
                        // Handle the error response here
                        // For example, you can log the error or show a message to the user
                        Log.e(TAG, "response.is Not Successful X")
                    }
                }

                override fun onFailure(call: Call<AddPostDataClass>, t: Throwable) {
                    // Handle the failure here
                    // For example, you can log the error or show a message to the user
                    Log.e(TAG, "onFailure ------->${t.message}")
                }
            })
            //*********************************************************

        }



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


    //------------------******
    private fun getFileFromUri(uri: Uri): File? {
        val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
        return documentFile?.let {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, documentFile.name ?: "temp_file")
            copyInputStreamToFile(inputStream, file)
            file
        }
    }

    private fun copyInputStreamToFile(inputStream: InputStream?, file: File) {
        try {
            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error copying InputStream to File", e)
        }
    }





}