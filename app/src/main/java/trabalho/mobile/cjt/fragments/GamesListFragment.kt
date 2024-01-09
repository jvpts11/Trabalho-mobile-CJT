package trabalho.mobile.cjt.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import trabalho.mobile.cjt.Game
import trabalho.mobile.cjt.R
import trabalho.mobile.cjt.User
import trabalho.mobile.cjt.databinding.FragmentGamesListBinding

class GamesListFragment : Fragment() {

    val allGamesList : MutableList<Game> = mutableListOf<Game>()

    private lateinit var binding : FragmentGamesListBinding

    private lateinit var dbGamesRef : DatabaseReference
    private lateinit var dbUsersRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGamesListBinding.inflate(inflater,container,false)

        dbUsersRef = FirebaseDatabase.getInstance().getReference("Users")

        dbGamesRef = FirebaseDatabase.getInstance().getReference("Games")
        dbGamesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (gameSnapshot in snapshot.children){
                        val game = gameSnapshot.getValue(Game::class.java)
                        allGamesList.add(game!!)
                    }

                    val listView = binding.allGamesList
                    listView.adapter = allGamesListAdapter(allGamesList, context, dbUsersRef, dbGamesRef)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Precisa estar aqui para a implementação de "object", apenas
            }

        })

        return binding.root
    }

    class allGamesListAdapter(val allGamesList : MutableList<Game>, context : Context?, val dbUsersRef : DatabaseReference,
        val dbGamesRef : DatabaseReference) : BaseAdapter(){

        private val mContext : Context?
        init{
            mContext = context
        }

        override fun getCount(): Int {
            return allGamesList.size
        }

        override fun getItem(position: Int): Any {
            return ""
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val allGamesItemView = layoutInflater.inflate(R.layout.all_games_item, parent, false)

            val gameName = allGamesItemView.findViewById<TextView>(R.id.all_games_name)
            val gameOwner = allGamesItemView.findViewById<TextView>(R.id.all_games_owner)

            val currID = allGamesList[position].gameOwnerID
            val auxRef = dbUsersRef.child(currID)

            auxRef.get().addOnSuccessListener {
                if (it.exists()){
                    gameOwner.text = it.child("name").value.toString()
                }
            }

            gameName.text = allGamesList[position].gameName

            return allGamesItemView
        }
    }

}