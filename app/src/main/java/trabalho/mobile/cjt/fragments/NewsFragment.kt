package trabalho.mobile.cjt.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import trabalho.mobile.cjt.ProfileActivity
import trabalho.mobile.cjt.R
import trabalho.mobile.cjt.databinding.FragmentMemberListBinding
import trabalho.mobile.cjt.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding

    private lateinit var profileAccessButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentNewsBinding.inflate(inflater, container, false)

        profileAccessButton = binding.profileAccessButton

        profileAccessButton.setOnClickListener(){
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}