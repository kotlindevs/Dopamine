package com.google.android.piyush.dopamine.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineUserProfile
import com.google.android.piyush.dopamine.activities.DopamineVideoWatchHistory
import com.google.android.piyush.dopamine.adapters.HomeAdapter
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.databinding.FragmentHomeBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.HomeViewModel
import com.google.android.piyush.youtube.viewModels.HomeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class Home : Fragment() {

    private var fragmentHomeBinding : FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: YoutubeRepositoryImpl
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding
        repository = YoutubeRepositoryImpl()
        homeViewModelFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        firebaseAuth = FirebaseAuth.getInstance()

        fragmentHomeBinding!!.greeting.text = getGreeting()

        //User details
        Log.d(TAG, firebaseAuth.currentUser?.displayName.toString())
        Log.d(TAG, firebaseAuth.currentUser?.email.toString())
        Log.d(TAG, firebaseAuth.currentUser?.photoUrl.toString())
        Log.d(TAG, firebaseAuth.currentUser?.uid.toString())
        Log.d(TAG, firebaseAuth.currentUser?.phoneNumber.toString())
        Log.d(TAG, firebaseAuth.currentUser?.providerId.toString())
        Log.d(TAG, firebaseAuth.currentUser?.isAnonymous.toString())
        Log.d(TAG, firebaseAuth.currentUser?.isEmailVerified.toString())
        Log.d(TAG, firebaseAuth.currentUser?.providerData.toString())
        Log.d(TAG, firebaseAuth.currentUser?.metadata.toString())

        if(firebaseAuth.currentUser?.email.toString().isEmpty()){
            Glide.with(this).load(SignInUtils.DEFAULT_IMAGE).into(fragmentHomeBinding!!.userImage)
        }else{
            Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(fragmentHomeBinding!!.userImage)
        }

        fragmentHomeBinding!!.watchHistory.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    DopamineVideoWatchHistory::class.java
                )
            )
        }

        fragmentHomeBinding!!.userImage.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    DopamineUserProfile::class.java
                )
            )
        }

        if(NetworkUtilities.isNetworkAvailable(requireContext())) {
            homeViewModel.videos.observe(viewLifecycleOwner) {videos ->
                when (videos) {
                    is YoutubeResource.Loading -> {
                        binding.shimmerRecyclerView.visibility = View.VISIBLE
                        binding.shimmerRecyclerView.startShimmer()
                        Log.d(TAG, "Loading: True")
                    }
                    is YoutubeResource.Success -> {
                        binding.shimmerRecyclerView.visibility = View.INVISIBLE
                        binding.shimmerRecyclerView.stopShimmer()
                        binding.recyclerView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            homeAdapter = HomeAdapter(requireContext(), videos.data)
                            adapter = homeAdapter
                        }
                        //Log.d(TAG, "Success: ${videos.data}")
                    }
                    is YoutubeResource.Error -> {
                        Log.d(TAG, "Error: ${videos.exception.message.toString()}")
                        MaterialAlertDialogBuilder(requireContext())
                            .apply {
                                this.setTitle("Something went wrong")
                                this.setMessage(videos.exception.message.toString())
                                this.setIcon(R.drawable.ic_dialog_error)
                                this.setCancelable(false)
                                this.setNegativeButton("Cancel") { dialog, _ ->
                                    dialog?.dismiss()
                                }
                                this.setPositiveButton("Retry") { _, _ ->
                                    homeViewModel.videos.observe(viewLifecycleOwner) {
                                        if(it is YoutubeResource.Success) {
                                            binding.shimmerRecyclerView.visibility = View.INVISIBLE
                                            binding.shimmerRecyclerView.stopShimmer()
                                            binding.recyclerView.apply {
                                                setHasFixedSize(true)
                                                layoutManager = LinearLayoutManager(context)
                                                homeAdapter = HomeAdapter(requireContext(),it.data)
                                                adapter = homeAdapter
                                            }
                                        }else{
                                            binding.shimmerRecyclerView.visibility = View.VISIBLE
                                            binding.shimmerRecyclerView.startShimmer()
                                            this.create().show()
                                        }
                                    }
                                }.create().show()
                            }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentHomeBinding = null
        homeViewModel.videos.removeObservers(viewLifecycleOwner)
    }

    private fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 6..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..23 -> "Good Evening"
            else -> "Good Night"
        }
    }
}