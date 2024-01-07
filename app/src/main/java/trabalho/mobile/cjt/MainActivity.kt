package trabalho.mobile.cjt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import trabalho.mobile.cjt.databinding.ActivityMainBinding
import trabalho.mobile.cjt.fragments.MemberListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navBar = binding.mainBottomNav
        navBar.setOnItemSelectedListener {

            when (it.itemId){
                R.id.menu_item_members -> replaceFragment(MemberListFragment())
                R.id.menu_item_suggestion -> swapActivity(Intent(this@MainActivity,SuggestionActivity::class.java))
                else -> {

                }
            }
            true
        }
    }

    private fun swapActivity(intent: Intent){
        startActivity(intent)
        finish()
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_frame_layout,fragment)
        fragmentTransaction.commit()
    }
}