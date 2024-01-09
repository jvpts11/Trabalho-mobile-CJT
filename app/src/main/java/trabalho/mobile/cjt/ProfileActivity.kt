package trabalho.mobile.cjt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import trabalho.mobile.cjt.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var uid : String
    lateinit var dbRef : DatabaseReference
    lateinit var user : User

    lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()){
            getUserData()
        }

        binding.goBackBtn.setOnClickListener{
            startActivity(Intent(this@ProfileActivity,MainActivity::class.java))
            finish()
        }

        binding.usernameApplyButton.setOnClickListener({
            dbRef.child(uid).child("name").setValue(binding.userNameEditText.text.toString())
        })
    }

    private fun getUserData() {
        dbRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.userNameEditText.setText(user.name)
                binding.attendanceNumber.setText(user.attendance.toString())

                val atd : Int = user.attendance
                val color : Int
                val level : String

                if (atd < 4) {
                    color = ContextCompat.getColor(applicationContext, R.color.vibrant_lv1color)
                    level = "1"
                }
                else if (atd >= 4 && atd < 9){
                    color = ContextCompat.getColor(applicationContext, R.color.vibrant_lv2color)
                    level = "2"
                }
                else if (atd >= 9 && atd < 15){
                    color = ContextCompat.getColor(applicationContext, R.color.vibrant_lv3color)
                    level = "3"
                }
                else if (atd >= 15 && atd < 22){
                    color = ContextCompat.getColor(applicationContext, R.color.vibrant_lv4color)
                    level = "4"
                }
                else if (atd >= 22){
                    color = ContextCompat.getColor(applicationContext, R.color.vibrant_lv5color)
                    level = "5"
                }
                else{
                    color = 0
                    level = "0"
                }

                binding.levelNumber.setTextColor(color)
                binding.levelNumber.text = level
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}