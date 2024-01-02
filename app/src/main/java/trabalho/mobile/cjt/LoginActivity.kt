package trabalho.mobile.cjt

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import org.w3c.dom.Text
import trabalho.mobile.cjt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var testeTexto : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        testeTexto = findViewById(R.id.textTeste)
    }

}