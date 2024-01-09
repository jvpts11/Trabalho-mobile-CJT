package trabalho.mobile.cjt.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import com.google.common.io.Resources.getResource
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import trabalho.mobile.cjt.R
import trabalho.mobile.cjt.User
import trabalho.mobile.cjt.databinding.FragmentMemberListBinding

class MemberListFragment : Fragment() {

    val memberList : MutableList<User> = mutableListOf<User>()

    private lateinit var binding: FragmentMemberListBinding

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentMemberListBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    memberList.clear()
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(User::class.java)
                        memberList.add(user!!)
                    }

                    val listView = binding.memberList
                    listView.adapter = memberListAdapter(memberList, context, dbRef, auth)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Precisa estar aqui para a implementação de "object", apenas
            }

        })

        return binding.root
    }

    class memberListAdapter(val memberList : MutableList<User>, context : Context?, val dbRef : DatabaseReference, val auth: FirebaseAuth) : BaseAdapter(){

        private val mContext : Context?
        init{
            mContext = context
        }

        override fun getCount(): Int {
            return memberList.size
        }

        override fun getItem(position: Int): Any {
            return ""
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val memberView = layoutInflater.inflate(R.layout.item_member, parent, false)

            val memberName = memberView.findViewById<TextView>(R.id.memberName)
            val memberAttendance = memberView.findViewById<TextView>(R.id.memberAttendance)
            val memberBackground = memberView.findViewById<View>(R.id.itemBackground)
            val addAtd = memberView.findViewById<ImageButton>(R.id.add_atd)
            val lowerAtd = memberView.findViewById<ImageButton>(R.id.lower_atd)

            memberName.text = memberList[position].name
            memberAttendance.text = memberList[position].attendance.toString()

            val atd : Int = memberList[position].attendance
            val color : Int

            if (atd < 4) {
                color = ContextCompat.getColor(mContext!!, R.color.lv1color)
            }
            else if (atd in 5..8){
                color = ContextCompat.getColor(mContext!!, R.color.lv2color)
            }
            else if (atd in 10..14){
                color = ContextCompat.getColor(mContext!!, R.color.lv3color)
            }
            else if (atd in 16..21){
                color = ContextCompat.getColor(mContext!!, R.color.lv4color)
            }
            else if (atd >= 22){
                color = ContextCompat.getColor(mContext!!, R.color.lv5color)
            }
            else{
                color = 0
            }

            memberBackground.setBackgroundColor(color)

            dbRef.child(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                if (it.exists()){
                    val isAdmin : Boolean? = it.child("admin").getValue(Boolean::class.java)
                    if (isAdmin == true){
                        addAtd.visibility = View.VISIBLE
                        lowerAtd.visibility = View.VISIBLE

                        addAtd.setOnClickListener{
                            var dbRefAux = FirebaseDatabase.getInstance().getReference("Users")
                            val auxId = memberList[position].userid
                            val dbRef = dbRefAux.child(auxId).child("attendance")
                            //val newValueT = (dbRef.toString().toInt() + 1).toString()
                            Toast.makeText(mContext, "!", Toast.LENGTH_SHORT).show()
                            val newValue = 1
                            dbRef.setValue(newValue)
                        }

                        lowerAtd.setOnClickListener{
                            var dbRefAux = FirebaseDatabase.getInstance().getReference("Users")
                            val auxId = memberList[position].userid
                            val dbRef = dbRefAux.child(auxId).child("attendance")
                            //val newValue = dbRef.toString().toInt() - 1
                            val newValue = 2
                            dbRef.setValue(newValue)
                        }
                    }
                }
            }

            return memberView
        }
    }
}