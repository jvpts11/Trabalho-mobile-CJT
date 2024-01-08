package trabalho.mobile.cjt

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import trabalho.mobile.cjt.databinding.ActivityUserGamesBinding
import trabalho.mobile.cjt.fragments.MemberListFragment

class UserGamesActivity : AppCompatActivity() {

    val userGames : MutableList<Game> = mutableListOf<Game>()

    lateinit var dbRef : DatabaseReference
    lateinit var auth : FirebaseAuth
    lateinit var uid : String

    lateinit var binding: ActivityUserGamesBinding

    lateinit var goBackButton : ImageButton
    lateinit var addUserGameButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goBackButton = binding.goBackButton
        addUserGameButton = binding.addGameButton

        goBackButton.setOnClickListener{
            startActivity(Intent(this@UserGamesActivity,MainActivity::class.java))
            finish()
        }

        addUserGameButton.setOnClickListener{
            showDialogBox()
        }

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("Games")
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    userGames.clear()
                    for (gameSnapshot in snapshot.children){
                        val game = gameSnapshot.getValue(Game::class.java)
                        if (game?.gameOwnerID == uid){
                            userGames.add(game!!)
                        }
                    }
                    val listView = binding.userGamesList
                    listView.adapter = UserGamesListAdapter(userGames, this@UserGamesActivity, dbRef)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //nada por aqui
            }

        })
    }

    private fun showDialogBox(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_user_game_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val gameNameEditText : EditText = dialog.findViewById(R.id.dialog_new_game_name)
        val cancelBtn : ImageButton = dialog.findViewById(R.id.dialog_cancel_button)
        val addUserGameBtn : ImageButton = dialog.findViewById(R.id.dialog_add_game_button)

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        addUserGameBtn.setOnClickListener{
            val gameNameText : String = gameNameEditText.text.toString()
            val newGame : Game = Game(gameNameText, uid)
            dbRef.child(gameNameText).setValue(newGame)
            dialog.dismiss()
        }

        dialog.show()
    }

    class UserGamesListAdapter(val gamesList : MutableList<Game>, context : Context?, databaseRef : DatabaseReference) : BaseAdapter(){

        private val dbRef : DatabaseReference
        private val mContext : Context?
        init{
            mContext = context
            dbRef = databaseRef
        }

        override fun getCount(): Int {
            return gamesList.size
        }

        override fun getItem(position: Int): Any {
            return ""
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val userGameView = layoutInflater.inflate(R.layout.user_game_item, parent, false)

            val gameName = userGameView.findViewById<TextView>(R.id.game_name)
            gameName.text = gamesList[position].gameName

            val deleteBtn = userGameView.findViewById<ImageButton>(R.id.delete_user_game_btn)
            deleteBtn.setOnClickListener {
                dbRef.child(gamesList[position].gameName).removeValue()
            }

            return userGameView
        }
    }
}