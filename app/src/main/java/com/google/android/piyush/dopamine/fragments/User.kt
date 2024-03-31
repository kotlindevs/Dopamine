package com.google.android.piyush.dopamine.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineYtSettings
import com.google.android.piyush.dopamine.adapters.RecentVideosAdapter
import com.google.android.piyush.dopamine.databinding.FragmentUserBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.firebase.auth.FirebaseAuth

class User : Fragment() {

    private var userFragment : FragmentUserBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseViewModel: DatabaseViewModel
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
        databaseViewModel = DatabaseViewModel(requireContext())

        if(NetworkUtilities.isNetworkAvailable(context = requireContext()).equals(true)) {
            if (firebaseAuth.currentUser?.email.isNullOrEmpty()) {
                Glide.with(this).load(R.drawable.default_user).into(binding.userImage)
                binding.userName.text = getString(R.string.app_name)
                binding.userEmail.text = firebaseAuth.currentUser?.phoneNumber
            } else {
                Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(binding.userImage)
                binding.userName.text = firebaseAuth.currentUser?.displayName
                binding.userEmail.text = firebaseAuth.currentUser?.email
            }
        }else{
            requireContext().getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE).apply {
                getString("uid","").also { binding.userName.text = if(it.isNullOrEmpty()) "No User Id" else it.substring(0,15) }
                getString("email","").also { binding.userEmail.text = if(it.isNullOrEmpty()) "Empty Email" else it }
                binding.userImage.apply {
                    setImageResource(R.drawable.default_user)
                }
            }
            ToastUtilities.showToast(
                requireContext(),"You are not connected to the internet"
            )
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.setting -> {
                    context?.startActivity(
                        Intent(context, DopamineYtSettings::class.java)
                    )
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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        userFragment = null
    }
}