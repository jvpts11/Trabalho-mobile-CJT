package trabalho.mobile.cjt.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import trabalho.mobile.cjt.ProfileActivity
import trabalho.mobile.cjt.User
import trabalho.mobile.cjt.UserGamesActivity
import trabalho.mobile.cjt.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    lateinit var auth : FirebaseAuth
    lateinit var uid : String
    lateinit var dbRef : DatabaseReference
    lateinit var user : User

    private lateinit var binding: FragmentNewsBinding

    private lateinit var profileAccessButton : Button
    private lateinit var userGamesAccessButton : Button

    private lateinit var newsImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()){
            getUserData()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentNewsBinding.inflate(inflater, container, false)

        profileAccessButton = binding.profileAccessButton
        userGamesAccessButton = binding.userGamesAccessButton
        newsImage = binding.newsImage

        profileAccessButton.setOnClickListener(){
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        userGamesAccessButton.setOnClickListener(){
            val intent = Intent(activity, UserGamesActivity::class.java)
            startActivity(intent)
        }

        newsImage.setOnClickListener {
            if(user.isAdmin) {
                Toast.makeText(this.context, "Registering a new Champion!", Toast.LENGTH_SHORT)
            }
        }

        return binding.root
    }

    private fun getUserData(){
        dbRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.profilePageTile.setText(user.name + "'s PAGE")
                binding.userGamesTile.setText(user.name + "'s GAMES")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}