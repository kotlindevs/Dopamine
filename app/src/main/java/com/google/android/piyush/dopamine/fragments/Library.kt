package com.google.android.piyush.dopamine.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineUserProfile
import com.google.android.piyush.dopamine.adapters.LibraryAdapter
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.databinding.FragmentLibraryBinding
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.LibraryViewModel
import com.google.android.piyush.youtube.viewModels.LibraryViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class Library : Fragment() {

    private var fragmentLibraryBinding: FragmentLibraryBinding? = null
    private lateinit var repository: YoutubeRepositoryImpl
    private lateinit var viewModel : LibraryViewModel
    private lateinit var viewModelProviderFactory: LibraryViewModelFactory
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var libraryAdapter: LibraryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLibraryBinding.bind(view)
        fragmentLibraryBinding = binding
        repository = YoutubeRepositoryImpl()
        viewModelProviderFactory = LibraryViewModelFactory(repository)
        viewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[LibraryViewModel::class.java]

        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser?.email.toString().isEmpty()){
            Glide.with(this).load(SignInUtils.DEFAULT_IMAGE).into(fragmentLibraryBinding!!.userImage)
        }else{
            Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(fragmentLibraryBinding!!.userImage)
        }

        fragmentLibraryBinding!!.userImage.setOnClickListener {
            Toast.makeText(context,firebaseAuth.currentUser!!.displayName,
                Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(context, DopamineUserProfile::class.java)
            )
        }

        viewModel.reverbAndSlowedVideos.observe(viewLifecycleOwner){ playListVideos ->
            when(playListVideos) {
                is YoutubeResource.Success -> {
                    libraryAdapter = LibraryAdapter(requireContext(), playListVideos.data)
                    Log.d(ContentValues.TAG,playListVideos.data.toString())
                    fragmentLibraryBinding?.apply {
                        editsYouLikedList.layoutManager =
                            LinearLayoutManager(
                                context,
                                editsYouLikedList.horizontalFadingEdgeLength,
                                false
                            )
                        editsYouLikedList.adapter = libraryAdapter
                        binding.editsYouLikedEffect.stopShimmer()
                        binding.editsYouLikedEffect.visibility = View.INVISIBLE
                    }
                }

                is YoutubeResource.Error -> {
                    showToast(playListVideos.exception.message.toString())
                }

                is YoutubeResource.Loading -> {
                    binding.editsYouLikedEffect.startShimmer()
                    binding.editsYouLikedEffect.visibility = View.VISIBLE
                }
            }
        }

        viewModel.gamingVideos.observe(viewLifecycleOwner){ playListVideos ->
            when(playListVideos) {
                is YoutubeResource.Success -> {
                    libraryAdapter = LibraryAdapter(requireContext(), playListVideos.data)
                    Log.d(ContentValues.TAG,playListVideos.data.toString())
                    fragmentLibraryBinding?.apply {
                        gamingList.layoutManager =
                            LinearLayoutManager(
                                context,
                                gamingList.horizontalFadingEdgeLength,
                                false
                            )
                        gamingList.adapter = libraryAdapter
                        binding.gamingEffect.stopShimmer()
                        binding.gamingEffect.visibility = View.INVISIBLE
                    }
                }

                is YoutubeResource.Error -> {
                    showToast(playListVideos.exception.message.toString())
                }

                is YoutubeResource.Loading -> {
                    binding.gamingEffect.startShimmer()
                    binding.gamingEffect.visibility = View.VISIBLE
                }
            }
        }

        viewModel.lofiBhajan.observe(viewLifecycleOwner){ playListVideos ->
            when(playListVideos) {
                is YoutubeResource.Success -> {
                    libraryAdapter = LibraryAdapter(requireContext(), playListVideos.data)
                    Log.d(ContentValues.TAG,playListVideos.data.toString())
                    fragmentLibraryBinding?.apply {
                        lofiBhajanList.layoutManager =
                            LinearLayoutManager(
                                context,
                                lofiBhajanList.horizontalFadingEdgeLength,
                                false
                            )
                        lofiBhajanList.adapter = libraryAdapter
                        binding.lofiBhajanEffect.stopShimmer()
                        binding.lofiBhajanEffect.visibility = View.INVISIBLE
                    }
                }

                is YoutubeResource.Error -> {
                    showToast(playListVideos.exception.message.toString())
                }

                is YoutubeResource.Loading -> {
                    binding.lofiBhajanEffect.startShimmer()
                    binding.lofiBhajanEffect.visibility = View.VISIBLE
                }
            }
        }

        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(0)
            .build()

        fragmentLibraryBinding!!.youtubePlayerView.enableAutomaticInitialization = false
        fragmentLibraryBinding!!.youtubePlayerView.initialize(
            object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.loadVideo(
                        "l2UDgpLz20M", 0.0F
                    )
                    youTubePlayer.mute()
                }
            },
            true,
            iFramePlayerOptions
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentLibraryBinding = null
        viewModel.reverbAndSlowedVideos.removeObservers(viewLifecycleOwner)
        viewModel.gamingVideos.removeObservers(viewLifecycleOwner)
        viewModel.lofiBhajan.removeObservers(viewLifecycleOwner)
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}