package com.google.android.piyush.dopamine.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.activities.DopamineYtSettings
import com.google.android.piyush.dopamine.activities.PhoneNumberAuthentication
import com.google.android.piyush.dopamine.adapters.CustomPlayListVAdapter
import com.google.android.piyush.dopamine.adapters.RecentVideosAdapter
import com.google.android.piyush.dopamine.authentication.User
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModelFactory
import com.google.android.piyush.dopamine.databinding.BottomSheetPlaylistBinding
import com.google.android.piyush.dopamine.databinding.FragmentUserBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
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
    private lateinit var recentVideosAdapter: RecentVideosAdapter
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
            binding.apply {
                text1.visibility = View.VISIBLE
                text2.visibility = View.VISIBLE
                googleSignIn.visibility = View.VISIBLE
                phoneAuth.visibility = View.VISIBLE
                emptyPlaylist.visibility = View.VISIBLE
                View.GONE.also {
                    userImage.visibility = it
                    userName.visibility = it
                    userEmail.visibility = it
                    watchHistory.visibility = it
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

        if(databaseViewModel.countTheNumberOfCustomPlaylist() < 1){
            userFragment!!.yourPlaylists.visibility = View.GONE
            userFragment!!.emptyPlaylist.visibility = View.VISIBLE
        }else{
            userFragment!!.emptyPlaylist.visibility = View.GONE
            userFragment!!.yourPlaylists.visibility = View.VISIBLE
            userFragment!!.yourPlaylists.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, binding.yourPlaylists.horizontalFadingEdgeLength, false)
                adapter = CustomPlayListVAdapter(requireContext(),databaseViewModel.getPlaylist())
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
            val realtimeViewModel = RealtimeViewModel()
            realtimeViewModel.getRecentVideos()
            realtimeViewModel.listOfRecentVideos.observe(viewLifecycleOwner) { recentVideos ->
                when(recentVideos){
                    is RealtimeResource.Loading -> {}
                    is RealtimeResource.Success -> {
                        binding.recentWatchHistory.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(
                                context,
                                binding.recentWatchHistory.horizontalFadingEdgeLength,
                                false
                            )
                            recentVideosAdapter = RecentVideosAdapter(context, recentVideos.data)
                            adapter = recentVideosAdapter.apply {
                                setVideos(
                                    recentVideos.data
                                )
                            }
                        }
                    }
                    is RealtimeResource.Error -> {
                        if (recentVideos.data.isNullOrEmpty()) {
                            binding.recentWatchHistory.visibility = View.GONE
                            binding.watchHistory.visibility = View.GONE
                        }else{
                            binding.recentWatchHistory.visibility = View.VISIBLE
                            binding.watchHistory.visibility = View.VISIBLE
                        }
                    }
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

                    val userId = Firebase.auth.currentUser?.uid.toString()
                    val userName = Firebase.auth.currentUser?.displayName.toString()
                    val userEmail = Firebase.auth.currentUser?.email.toString()
                    val userPhotoUrl = Firebase.auth.currentUser?.photoUrl.toString()

                    val database = RealtimeViewModel()

                    database.isUserExists(
                        User(
                            userId,
                            userName,
                            userEmail,
                            userPhotoUrl
                        )
                    )
                    database.dopamineUser.observe(viewLifecycleOwner) {
                        if(it is RealtimeResource.Success){
                            val user = it.data
                            requireContext().getSharedPreferences("currentUser",
                                AppCompatActivity.MODE_PRIVATE
                            ).edit()
                                .putString("uid", user?.userId)
                                .putString("name", user?.userName)
                                .putString("email", user?.userEmail)
                                .putString("photoUrl", user?.userImage)
                                .apply()
                        }

                        if(it is RealtimeResource.Error){
                            Log.d(TAG, "Error: ${it.message}")
                        }
                    }

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
            if(NetworkUtilities.isNetworkAvailable(requireContext())) {
                lifecycleScope.launch {
                    val signInIntentSender = userRepository.googleSignIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            }else{
                val googleAuthNoNetwork = GoogleAuthNoNetwork()
                googleAuthNoNetwork.show(parentFragmentManager, googleAuthNoNetwork.tag)
            }
        }

        binding.phoneAuth.setOnClickListener{
            startActivity(Intent(requireContext(), PhoneNumberAuthentication::class.java))
        }

        binding.addPlaylist.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(parentFragmentManager, modalBottomSheet.tag)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        userFragment = null
    }
}


class ModalBottomSheet : BottomSheetDialogFragment() {

    private var modalBottomSheet: BottomSheetPlaylistBinding? = null
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_playlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetPlaylistBinding.bind(view)
        modalBottomSheet = binding
        databaseViewModel = DatabaseViewModel(requireContext())

        val playlistName = binding.playlistName.text
        val playlistDescription = binding.playlistDescription.text

        binding.addPlaylist.setOnClickListener {
            if(databaseViewModel.isPlaylistExist(playlistName.toString())){
                binding.playlistNameInputLayout.isErrorEnabled = true
                binding.playlistNameInputLayout.error = "Playlist Already Exists"
            }else{
                if(playlistName?.isEmpty()!!.equals(true)){
                    ToastUtilities.showToast(context, "Please Fill All Fields")
                }else {
                    databaseViewModel.createCustomPlaylist(
                        CustomPlaylistView(
                            playlistName.toString(),
                            playlistDescription.toString().ifEmpty { "Empty Description" },
                        )
                    )
                    playlistName.clear()
                    playlistDescription?.clear()
                    ToastUtilities.showToast(context, "$playlistName Created ✅")
                    this.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        modalBottomSheet = null
    }
}

class GoogleAuthNoNetwork : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.google_auth_no_network, container, false)
}
