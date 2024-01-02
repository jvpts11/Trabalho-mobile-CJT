package trabalho.mobile.cjt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {


    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailText = findViewById(R.id.register_editText_email)
        passwordText = findViewById(R.id.register_editText_password)
        registerButton = findViewById(R.id.register_register_button)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            var email_text = emailText.text.toString()
            var password_text = passwordText.text.toString()

            if(TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text)){
                Toast.makeText(this,"Credenciais Vazias!",Toast.LENGTH_SHORT)
            }else if(password_text.length < 5){
                Toast.makeText(this,"Palavra-Passe muito curta!",Toast.LENGTH_SHORT)
            } else {
                registerUser(email_text,password_text)
            }
        }
    }

    private fun registerUser(email_Text : String, passwordText : String){
        auth.createUserWithEmailAndPassword(email_Text,passwordText).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Novo Guerreiro para o clube criado!", Toast.LENGTH_SHORT)
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this,"Falha no Registo", Toast.LENGTH_SHORT)
            }
        }
    }
}