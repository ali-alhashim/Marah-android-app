package sa.com.marah


import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import sa.com.marah.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)



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


        val homeFragment = HomeFragment()
        openFragment(homeFragment)


          binding.TopNavigationMenu.setOnItemSelectedListener { item ->
              when(item.itemId)
              {
                R.id.cities ->  Toast.makeText(this, "show cities list", Toast.LENGTH_LONG).show()
                R.id.filter_category -> Toast.makeText(this, "show Categories", Toast.LENGTH_LONG).show()
                R.id.your_location ->    Toast.makeText(this, "get your location and select nearst city", Toast.LENGTH_LONG).show()
              }
              true
          }







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