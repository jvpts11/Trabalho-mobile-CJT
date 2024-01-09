package trabalho.mobile.cjt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import trabalho.mobile.cjt.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //binding da RegisterActivity
    private lateinit var binding: ActivityRegisterBinding

    //Campos no layout da RegisterActivity
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var username: EditText
    private lateinit var registerButton: Button

    //Referências ao autenticador e a database do Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailText = binding.registerEditTextEmail
        passwordText = binding.registerEditTextPassword
        registerButton = binding.registerRegisterButton
        username = binding.registerEditTextUsername

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        registerButton.setOnClickListener {
            var email_text = emailText.text.toString()
            var password_text = passwordText.text.toString()
            var username_text = username.text.toString()

            Toast.makeText(this,"Palavra-passe muito curta!", Toast.LENGTH_SHORT)

            if(TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text)){
                Toast.makeText(this,"Credenciais vazias!", Toast.LENGTH_SHORT)
            }else if(password_text.length < 5){
                Toast.makeText(this,"Palavra-passe muito curta!", Toast.LENGTH_SHORT)
            } else {
                registerUser(email_text, password_text, username_text)
            }
        }
    }

    //Registra um novo e-mail pelo processo de autenticação
    private fun registerUser(email_Text : String, passwordText : String, usernameText : String){
        auth.createUserWithEmailAndPassword(email_Text,passwordText).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Novo guerreiro do clube criado!", Toast.LENGTH_SHORT)

                //Define o id do objeto User na database como igual ao id do e-mail registrado
                val userId : String = task.getResult().user?.uid ?: "null"
                saveUserObject(userId, usernameText)

                //Muda para a activity de login
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this,"Falha no Registo", Toast.LENGTH_SHORT)
            }
        }
    }

    //Cria na database um novo objeto do tipo User
    private fun saveUserObject(userId: String, username: String){
        val user = User(username, userId)
        dbRef.child(userId).setValue(user)
    }
}