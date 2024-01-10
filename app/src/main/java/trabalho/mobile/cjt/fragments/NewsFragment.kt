package trabalho.mobile.cjt.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsFragment : Fragment() {

    lateinit var auth : FirebaseAuth
    lateinit var uid : String
    lateinit var dbRef : DatabaseReference
    lateinit var user : User

    private lateinit var binding: FragmentNewsBinding

    private lateinit var profileAccessButton : Button
    private lateinit var userGamesAccessButton : Button

    private lateinit var uploadButton: ImageButton
    private lateinit var newsImage : ImageView

    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()){
            getUserData()
        }

        storageRef = FirebaseStorage.getInstance().getReference("News")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentNewsBinding.inflate(inflater, container, false)

        profileAccessButton = binding.profileAccessButton
        userGamesAccessButton = binding.userGamesAccessButton
        newsImage = binding.newsImage
        uploadButton = binding.newsButtonUpload

        getLatestImageFromFirebase(newsImage)

        profileAccessButton.setOnClickListener(){
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        userGamesAccessButton.setOnClickListener(){
            val intent = Intent(activity, UserGamesActivity::class.java)
            startActivity(intent)
        }

        dbRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                if(user.isAdmin){
                    uploadButton.visibility = View.VISIBLE
                    uploadButton.setOnClickListener {
                        selectAndUploadImage()
                    }

                } else{
                    uploadButton.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

    private fun selectAndUploadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    private fun getLatestImageFromFirebase(imageView: ImageView) {
        storageRef.listAll().addOnSuccessListener { result ->
            val latestImage = result.items.lastOrNull()

            if (latestImage != null) {
                latestImage.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(imageView)
                }.addOnFailureListener {
                    Log.d("FirebaseStorage", "Imagem obtida da firebase: ${it.message}")
                }
            } else {
                Log.e("FirebaseStorage", "Nennhuma imagem obtida da pasta news!")
            }
        }.addOnFailureListener {exception->
            Log.e("FirebaseStorage", "Erro ao obter lista de itens", exception)
        }
    }

    private fun uploadImage(uri: Uri) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading File....")
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val fileName = formatter.format(Date())
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                newsImage.setImageURI(null)
                Toast.makeText(requireContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                getLatestImageFromFirebase(newsImage)
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
            .addOnFailureListener { e ->
                if (progressDialog.isShowing) progressDialog.dismiss()
                Log.e("FirebaseStorage", "Failed to Upload", e)
                Toast.makeText(requireContext(), "Failed to Upload", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            newsImage.setImageURI(imageUri)
            imageUri?.let {
                uploadImage(it)
            }
        }
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