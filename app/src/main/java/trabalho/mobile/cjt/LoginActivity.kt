package trabalho.mobile.cjt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import trabalho.mobile.cjt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //Components of the Activity
    private lateinit var emailText : EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailText = findViewById(R.id.login_editText_email)
        passwordText = findViewById(R.id.login_editText_password)
        loginButton = findViewById(R.id.login_login_button)
        registerButton = findViewById(R.id.login_register_button)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            var txt_email = emailText.text.toString()
            var txt_password = passwordText.text.toString()
            loginUser(txt_email,txt_password)
        }

        registerButton.setOnClickListener {
            Toast.makeText(this, "Registering a new Champion!", Toast.LENGTH_SHORT)
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginUser(txtEmail:String, txtPassword:String){
        auth.signInWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(this) { task->
            if(task.isSuccessful){
                Toast.makeText(this, "Entrando no CJT APP", Toast.LENGTH_SHORT)
                val intentToMain = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intentToMain)
                finish()
            }else{
                Toast.makeText(this,"Falha no login! verifique suas credenciais inseridas",Toast.LENGTH_SHORT)
            }
        }
    }
}