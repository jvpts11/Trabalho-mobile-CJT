package trabalho.mobile.cjt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import trabalho.mobile.cjt.Game
import trabalho.mobile.cjt.R
import trabalho.mobile.cjt.databinding.FragmentGamesListBinding

class GamesListFragment : Fragment() {

    val gamesList : MutableList<Game> = mutableListOf<Game>()

    private lateinit var binding : FragmentGamesListBinding

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGamesListBinding.inflate(inflater,container,false)

        return binding.root
    }

}