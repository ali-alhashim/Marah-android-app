package sa.com.marah

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.com.marah.Data.ApiServiceLogin
import sa.com.marah.Data.LoginDataClass


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)


        val loginBtn:Button = view.findViewById(R.id.btn_login)
        val username :TextInputEditText = view.findViewById(R.id.username)
        val password :TextInputEditText = view.findViewById(R.id.password)

        loginBtn.setOnClickListener(){
            Log.d(TAG, "you clicked on login button with username => ${username.text.toString()} , and password ${password.text.toString()}")
            //----------------- send post request --------------
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity().BASE_URL)  // Replace with your API's base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiServiceLogin::class.java)
            val call = apiService.login(username.text.toString(), password.text.toString())

            call.enqueue(object : Callback<LoginDataClass> {
                override fun onResponse( call: Call<LoginDataClass>, response: Response<LoginDataClass>)
                {
                    if (response.isSuccessful)
                    {

                        Log.d(TAG, "json: ${response.body().toString()}")
                        // Save the token or navigate to the next screen
                       if( response.body()?.status =="success")
                       {



                           val preferences = requireActivity().getSharedPreferences("Marah.com.sa", Context.MODE_PRIVATE)
                           val editor = preferences.edit()
                           editor.putString("token", response.body()?.token)
                           editor.putString("username", response.body()?.username)
                           editor.apply()

                           Toast.makeText(requireContext()," يالله حيهم "+response.body()?.username, Toast.LENGTH_LONG).show()

                           (requireActivity() as MainActivity).setupUserName()
                           (requireActivity() as MainActivity).openFragment(HomeFragment())

                       }
                        else
                       {
                         Toast.makeText(requireContext(),response.body()?.message, Toast.LENGTH_LONG).show()
                       }
                    }
                    else
                    {
                        Log.e(TAG, "Login failed. HTTP error code: ${response.code()}")
                        // Handle the error response
                    }
                }
                override fun onFailure(call: Call<LoginDataClass>, t: Throwable)
                {
                   Log.e(TAG,"problem can't connect to server !")
                }

            })
            //---------------------------------------------------
        }


        return view
    }


}