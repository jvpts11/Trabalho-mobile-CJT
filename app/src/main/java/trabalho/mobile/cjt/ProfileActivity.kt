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
    }

    private fun getUserData() {
        dbRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.userNameEditText.setText(user.name)
                binding.attendanceNumber.setText(user.attendance.toString())

                val atd : Int = user.attendance
                val color : Int

                if (atd < 4) {
                    color = ContextCompat.getColor(applicationContext, R.color.lv1color)
                }
                else if (atd in 5..8){
                    color = ContextCompat.getColor(applicationContext, R.color.lv2color)
                }
                else if (atd in 10..14){
                    color = ContextCompat.getColor(applicationContext, R.color.lv3color)
                }
                else if (atd in 16..21){
                    color = ContextCompat.getColor(applicationContext, R.color.lv4color)
                }
                else if (atd >= 22){
                    color = ContextCompat.getColor(applicationContext, R.color.lv5color)
                }
                else{
                    color = 0
                }

                binding.levelNumber.setTextColor(color)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}