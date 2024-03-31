package com.google.android.piyush.dopamine.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.activities.DopamineYtSettings
import com.google.android.piyush.dopamine.activities.PhoneNumberAuthentication
import com.google.android.piyush.dopamine.adapters.RecentVideosAdapter
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModelFactory
import com.google.android.piyush.dopamine.databinding.FragmentUserBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class User : Fragment() {

    private var userFragment : FragmentUserBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var userRepository : UserAuthRepositoryImpl
    private lateinit var userViewModelFactory: UserAuthViewModelFactory
    private lateinit var userViewModel: UserAuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentUserBinding.bind(view)
        userFragment = binding
        firebaseAuth = FirebaseAuth.getInstance()
        userRepository = UserAuthRepositoryImpl(context = requireContext())
        userViewModelFactory = UserAuthViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserAuthViewModel::class.java]
        databaseViewModel = DatabaseViewModel(requireContext())

        if(firebaseAuth.currentUser?.uid.isNullOrEmpty()) {
            Log.d(TAG, "onViewCreated: ${firebaseAuth.currentUser?.uid} : 😉")
            binding.apply {
                text1.visibility = View.VISIBLE
                text2.visibility = View.VISIBLE
                googleSignIn.visibility = View.VISIBLE
                phoneAuth.visibility = View.VISIBLE
                View.GONE.also {
                    userImage.visibility = it
                    userName.visibility = it
                    userEmail.visibility = it
                }
            }
        }

        if(NetworkUtilities.isNetworkAvailable(context = requireContext()).equals(true)) {
            if(!firebaseAuth.currentUser?.uid.isNullOrEmpty()) {
                if (firebaseAuth.currentUser?.email.isNullOrEmpty()) {
                    binding.apply {
                        View.VISIBLE.also {
                            userImage.visibility = it
                            userName.visibility = it
                            userEmail.visibility = it
                        }
                        View.GONE.also {
                            text1.visibility = it
                            text2.visibility = it
                            googleSignIn.visibility = it
                            phoneAuth.visibility = it
                        }
                    }
                    Glide.with(this).load(R.drawable.default_user).into(binding.userImage)
                    binding.userName.text = getString(R.string.app_name)
                    binding.userEmail.text = firebaseAuth.currentUser?.phoneNumber
                } else {
                    binding.apply {
                        View.VISIBLE.also {
                            userImage.visibility = it
                            userName.visibility = it
                            userEmail.visibility = it
                        }
                        View.GONE.also {
                            text1.visibility = it
                            text2.visibility = it
                            googleSignIn.visibility = it
                            phoneAuth.visibility = it
                        }
                    }
                    Glide.with(this).load(firebaseAuth.currentUser?.photoUrl)
                        .into(binding.userImage)
                    binding.userName.text = firebaseAuth.currentUser?.displayName
                    binding.userEmail.text = firebaseAuth.currentUser?.email
                }
            }
        }else{
            requireContext().getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE).apply {
                getString("uid","").also { binding.userName.text = if(it.isNullOrEmpty()) "No User Id" else it.substring(0,15) }
                getString("email","").also { binding.userEmail.text = if(it.isNullOrEmpty()) "Empty Email" else it }
                binding.userImage.apply {
                    setImageResource(R.drawable.default_user)
                }
            }
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.setting -> {
                    context?.startActivity(
                        Intent(context, DopamineYtSettings::class.java)
                    )
                    true
                }
                R.id.notification -> {
                    true
                }
                R.id.search -> {
                    true
                }
                R.id.screencast -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        if(NetworkUtilities.isNetworkAvailable(requireContext()).equals(true)) {
            databaseViewModel.getRecentVideos()

            databaseViewModel.recentVideos.observe(viewLifecycleOwner) { recentVideos ->
                binding.recentWatchHistory.apply {
                    setHasFixedSize(true)
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, binding.recentWatchHistory.horizontalFadingEdgeLength, false)
                    adapter = RecentVideosAdapter(context, recentVideos)
                }
                if (recentVideos.isNullOrEmpty()) {
                    binding.recentWatchHistory.visibility = View.GONE
                }
            }
        }

        val launcher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = userRepository.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    userViewModel.onSignInResult(signInResult)
                }
            }else{
                ToastUtilities.showToast(
                    requireContext(),"Sign In Failed"
                )
            }
        }

        lifecycleScope.launch {
            userViewModel.state.collect { state ->
                if(state.isSignInSuccessful){
                    requireContext().getSharedPreferences("currentUser",
                        AppCompatActivity.MODE_PRIVATE
                    ).edit()
                        .putString("uid", Firebase.auth.currentUser?.uid)
                        .putString("name", Firebase.auth.currentUser?.displayName)
                        .putString("email", Firebase.auth.currentUser?.email)
                        .putString("photoUrl", Firebase.auth.currentUser?.photoUrl.toString())
                        .apply()
                    startActivity(
                        Intent(requireContext(), DopamineHome::class.java).putExtra("userSignedIn", true)
                    )
                    userViewModel.resetSignInState()
                }

                state.signInError?.let { error ->
                    ToastUtilities.showToast(
                        requireContext(), error
                    )
                }
            }
        }

        binding.googleSignIn.setOnClickListener{
            lifecycleScope.launch {
                val signInIntentSender = userRepository.googleSignIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }

        binding.phoneAuth.setOnClickListener{
            startActivity(Intent(requireContext(), PhoneNumberAuthentication::class.java))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        userFragment = null
    }
}