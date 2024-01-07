package trabalho.mobile.cjt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SuggestionActivity : AppCompatActivity() {

    private lateinit var suggestionButton : Button
    private lateinit var suggestionEditText: EditText

    private lateinit var dbref : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion)

        suggestionButton = findViewById(R.id.suggestion_button)
        suggestionEditText = findViewById(R.id.suggestion_text_field)

        dbref = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        suggestionButton.setOnClickListener {
            var suggestionText = suggestionEditText.text.toString()

            if (TextUtils.isEmpty(suggestionText)) {
                Toast.makeText(this, "Insira uma sugestão!", Toast.LENGTH_SHORT).show()
            } else {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userId = currentUser.uid

                    // Obter o nome do usuário a partir da base de dados
                    dbref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userName = snapshot.child("name").getValue(String::class.java)

                            if (!userId.isNullOrEmpty() && !userName.isNullOrEmpty()) {
                                registerSuggestion(userId, userName, suggestionText)
                            } else {
                                Toast.makeText(
                                    this@SuggestionActivity,
                                    "Erro ao obter informações do usuário.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@SuggestionActivity,
                                "Erro ao obter informações do usuário.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerSuggestion(userId: String, userName: String, suggestion: String) {
        // Criar um novo objeto Suggestions usando o ID do usuário como chave
        val suggestionsRef = FirebaseDatabase.getInstance().getReference("Suggestions").child(userId)

        // Adicionar campos do objeto Suggestions
        suggestionsRef.child("userName").setValue(userName)
        suggestionsRef.child("suggestion").setValue(suggestion)

        Toast.makeText(this, "Sugestão registrada com sucesso!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@SuggestionActivity, MainActivity::class.java))
        finish()  // Finalizar a atividade após o registro da sugestão
    }
}