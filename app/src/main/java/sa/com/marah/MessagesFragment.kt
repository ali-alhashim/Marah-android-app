package sa.com.marah

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MessagesFragment : Fragment() {
    private lateinit var MessagesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_messages, container, false)
        MessagesRecyclerView = view.findViewById(R.id.MessagesRecyclerView)

        loadMessagesList()
        return view
    }

    fun loadMessagesList()
    {

    }


}