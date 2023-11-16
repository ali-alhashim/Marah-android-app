package sa.com.marah
import android.app.Dialog
import android.content.ClipData.Item
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import sa.com.marah.databinding.ActivityMainBinding

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import sa.com.marah.Data.ApiServiceLocations
import sa.com.marah.Data.LocationsDataClass


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val BASE_URL:String = "http://192.168.8.114:8000/"
    private  lateinit var binding: ActivityMainBinding
    private val INTERNET_PERMISSION_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        // Check if the internet permission is granted
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED )
        {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.INTERNET"),
                INTERNET_PERMISSION_REQUEST_CODE
            )
        }





        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        toggle.drawerArrowDrawable.setColor(getColor(R.color.black))
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null



        binding.bottomNavigation.setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.btn_favort -> openFragment(FavoriteFragment())
                R.id.btn_home -> openFragment(HomeFragment())
                R.id.btn_add_post -> openFragment(AddPostFragment())
                R.id.btn_messages -> openFragment(MessagesFragment())
                R.id.btn_search -> openFragment(SearchFragment())
            }
            true

        }

        binding.bottomNavigation.selectedItemId = R.id.btn_home


        val homeFragment = HomeFragment()
        openFragment(homeFragment)


          binding.TopNavigationMenu.setOnItemSelectedListener { item ->
              when(item.itemId)
              {
                R.id.cities ->  showCitiesSheet()
                R.id.filter_category -> Toast.makeText(this, "show Categories", Toast.LENGTH_LONG).show()
                R.id.your_location ->    Toast.makeText(this, "get your location and select nearst city", Toast.LENGTH_LONG).show()
              }
              true
          }








    } // end onCreate



   private fun handleCityButtonClick(citiyId:Int)
   {
       Log.i(TAG,"you click on cite Id : ${citiyId}")
   }



    private  fun showCitiesSheet() {
        val dialog:Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout_cities)

        val CitiesLayout:LinearLayout = dialog.findViewById(R.id.CitiesLinearLayout)

        //---------------------------------
        val api = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiServiceLocations::class.java)
        api.getLocations().enqueue(object : Callback<List<LocationsDataClass>>{
            override fun onResponse(
                call: Call<List<LocationsDataClass>>,
                response: Response<List<LocationsDataClass>>
            ) {
                if(response.isSuccessful)
                {
                    Log.d(TAG, "Http get request is Successful for locations")
                    response.body()?.let{
                        for (location in it)
                        {
                            Log.i(TAG, "on Response  location Name:${location.name} with Id: ${location.id}")
                            // create Buttons with citiy name and add click lisetner with id parameter
                            val cityButton = Button(this@MainActivity)
                            cityButton.text = location.name
                            cityButton.setOnClickListener {
                                // Handle button click, you can use location.id here
                                // For example, pass it to another function or start a new activity
                                handleCityButtonClick(location.id)
                            }
                            CitiesLayout.addView(cityButton)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<LocationsDataClass>>, t: Throwable) {

                Log.d(TAG, "Http get request Failure for locations ${t.message} ............${api.toString()}")
            }

        })
        //---------------------------------


        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)


    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
          R.id.btn_account -> openFragment(MyAccountFragment())
          R.id.btn_myPost  -> openFragment(MyPostsFragment())
          R.id.btn_login   -> openFragment(LoginFragment())


        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }





    private fun openFragment(fragment:Fragment)
    {
        Log.d(TAG, "openFragment: " + fragment.javaClass.simpleName)

           try {
               val fragmentTransaction = supportFragmentManager.beginTransaction()
               fragmentTransaction.replace(R.id.fragment_container, fragment).commit()
               Log.d(TAG, "Fragment transaction committed")
           }
           catch(e:Exception)
           {
               Log.d(TAG, e.toString())
           }


    }


}