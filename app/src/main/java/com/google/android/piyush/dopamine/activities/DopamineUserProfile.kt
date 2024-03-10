package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.databinding.ActivityDopamineUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class DopamineUserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityDopamineUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(firebaseAuth.currentUser?.email.toString().isEmpty()){
            Glide.with(this).load(SignInUtils.DEFAULT_IMAGE).into(binding.userImage)
            binding.userName.text = getString(R.string.app_name)
            binding.userEmail.text = firebaseAuth.currentUser?.phoneNumber
        }else{
            Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(binding.userImage)
            binding.userName.text = firebaseAuth.currentUser?.displayName
            binding.userEmail.text = firebaseAuth.currentUser?.email
        }

        binding.topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, DopamineHome::class.java))
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout ->{

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Sign out from your account ?")
                        .setIcon(R.drawable.ic_dopamine)
                        .setMessage("Logging out will remove your account from the app and you will not be able to access it's features. To access it, please sign in again 😊")
                        .setCancelable(true)
                        .setPositiveButton("Yes"){
                                dialog, _ ->
                            firebaseAuth.signOut()
                            Toast.makeText(applicationContext,"See you soon 🫡", Toast.LENGTH_LONG).show()
                            startActivity(
                                Intent(this, MainActivity::class.java)
                            )
                            dialog.dismiss()
                        }
                        .setNegativeButton("No"){
                                dialog, _ ->
                            dialog.dismiss()
                        }
                  .create().show()
                    true
                }
                else -> false
            }
        }

        binding.customPlayList.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager,bottomSheetFragment.tag)
        }
    }

    class MyBottomSheetFragment : BottomSheetDialogFragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val view = inflater.inflate(R.layout.bottom_sheet_add_playlist,container,false)

            val playListName : TextInputEditText = view.findViewById(R.id.etPlaylistName)
            val createPlaylist = view.findViewById<MaterialButton>(R.id.btCreatePlaylist)
            createPlaylist.setOnClickListener {
                if(playListName.text.toString().isEmpty()) {
                    showToast("Playlist Name Cannot Be Empty :(")
                } else {
                    showToast("Under Development")
                }
            }

            return view
        }

        private fun showToast(message: String) {
            Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
        }
    }
}