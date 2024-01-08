package trabalho.mobile.cjt.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import trabalho.mobile.cjt.ProfileActivity
import trabalho.mobile.cjt.R
import trabalho.mobile.cjt.User
import trabalho.mobile.cjt.databinding.FragmentMemberListBinding
import trabalho.mobile.cjt.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    lateinit var auth : FirebaseAuth
    lateinit var uid : String
    lateinit var dbRef : DatabaseReference
    lateinit var user : User

    private lateinit var binding: FragmentNewsBinding

    private lateinit var profileAccessButton : Button

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

        profileAccessButton.setOnClickListener(){
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getUserData(){
        dbRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.profilePageTile.setText(user.name + "'s PAGE")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}