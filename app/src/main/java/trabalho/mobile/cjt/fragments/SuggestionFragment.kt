package trabalho.mobile.cjt.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import trabalho.mobile.cjt.MainActivity
import trabalho.mobile.cjt.R

class SuggestionFragment : Fragment() {

    private lateinit var suggestionButton: Button
    private lateinit var suggestionEditText: EditText

    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_suggestion, container, false)

        suggestionButton = view.findViewById(R.id.suggestion_button)
        suggestionEditText = view.findViewById(R.id.suggestion_text_field)

        dbref = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        suggestionButton.setOnClickListener {
            var suggestionText = suggestionEditText.text.toString()

            if (TextUtils.isEmpty(suggestionText)) {
                Toast.makeText(requireContext(), "Insira uma sugestão!", Toast.LENGTH_SHORT).show()
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
                                    requireContext(),
                                    "Erro ao obter informações do usuário.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                "Erro ao obter informações do usuário.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view;
    }

    private fun registerSuggestion(userId: String, userName: String, suggestion: String) {
        // Criar um novo objeto Suggestions usando o ID do usuário como chave
        val suggestionsRef = FirebaseDatabase.getInstance().getReference("Suggestions").child(userId)

        // Adicionar campos do objeto Suggestions
        suggestionsRef.child("userName").setValue(userName)
        suggestionsRef.child("suggestion").setValue(suggestion)

        Toast.makeText(requireContext(), "Sugestão registrada com sucesso!", Toast.LENGTH_SHORT).show()
    }

}