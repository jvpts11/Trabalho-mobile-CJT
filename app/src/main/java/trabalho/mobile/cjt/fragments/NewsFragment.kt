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

    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data

            // Verifica se a URI da imagem selecionada é válida
            if (selectedImageUri != null) {
                // A imagem já foi selecionada, pode fazer o upload para o Firebase aqui se necessário
                uploadImageToFirebase(selectedImageUri!!)
            } else {
                // A imagem ainda não foi selecionada, permita que o usuário escolha uma
                selectImage()
            }

            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(newsImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()){
            getUserData()
        }

        storageRef = FirebaseStorage.getInstance().reference.child("News")
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
            // Verifica se a URI da imagem selecionada é válida
            if (selectedImageUri != Uri.EMPTY) {
                // A imagem já foi selecionada, pode fazer o upload para o Firebase aqui se necessário
                uploadImageToFirebase(selectedImageUri!!)
            } else {
                // A imagem ainda não foi selecionada, permita que o usuário escolha uma
                selectImage()
            }
        }

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val imageName = getFileName(uri) // Obter o nome da imagem a partir da URI
        val imageRef = storageRef.child(imageName)

        // Upload da imagem para o Firebase Storage
        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // Imagem foi carregada com sucesso
                Toast.makeText(requireContext(), "Imagem carregada com sucesso!", Toast.LENGTH_SHORT).show()

                // Você pode obter a URL da imagem carregada para salvar no banco de dados, se necessário
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    // Aqui você pode salvar a URL da imagem no banco de dados se necessário
                    // Exemplo: saveImageUrlToDatabase(imageUrl)
                }
            }
            .addOnFailureListener { e ->
                // O upload da imagem falhou
                Toast.makeText(requireContext(), "Falha ao carregar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileName(uri: Uri): String {
        // Obter o nome do arquivo a partir da URI
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val displayName = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
            return displayName ?: "user_image.jpg" // Nome padrão caso não seja possível obter o nome
        }
        return "user_image.jpg" // Nome padrão caso ocorra algum problema
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