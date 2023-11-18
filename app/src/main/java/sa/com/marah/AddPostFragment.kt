package sa.com.marah

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner


class AddPostFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        val post_Category_spinner: Spinner = view.findViewById(R.id.post_category_Spinner)
        val options = arrayOf("Option 1", "Option 2", "Option 3")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        post_Category_spinner.adapter = adapter
         return view
    }


}