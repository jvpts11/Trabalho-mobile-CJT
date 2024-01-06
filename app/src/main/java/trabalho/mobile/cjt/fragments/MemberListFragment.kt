package trabalho.mobile.cjt.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentMemberListBinding.inflate(inflater, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(User::class.java)
                        memberList.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Precisa estar aqui para a implementação de "object", apenas
            }

        })

        val listView = binding.memberList
        listView.adapter = memberListAdapter(memberList, this.context)

        return binding.root
    }

    class memberListAdapter(val memberList : MutableList<User>, context : Context?) : BaseAdapter(){

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

            memberName.text = memberList[position].name
            memberAttendance.text = memberList[position].attendance.toString()

            return memberView
        }
    }
}