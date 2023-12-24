package sa.com.marah

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MyAccountFragment : Fragment() {
     private lateinit var userTextName:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_account, container, false)
        userTextName = view.findViewById(R.id.userTextName)
        val mainActivity = activity as? MainActivity
        val username = mainActivity?.getCurrentUser()
        userTextName.text = "Name : ${username}"
        return view
    }


}