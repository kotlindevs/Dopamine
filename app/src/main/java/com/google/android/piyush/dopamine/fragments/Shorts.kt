package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.ShortsAdapter
import com.google.android.piyush.dopamine.databinding.FragmentShortsBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.ShortsViewModel
import com.google.android.piyush.youtube.viewModels.ShortsViewModelFactory

class Shorts : Fragment() {

    private var shortsFragmentBinding: FragmentShortsBinding? = null
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var shortsViewModel: ShortsViewModel
    private lateinit var shortsViewModelFactory: ShortsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_shorts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShortsBinding.bind(view)
        shortsFragmentBinding = binding
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        shortsViewModelFactory = ShortsViewModelFactory(youtubeRepositoryImpl)
        shortsViewModel = ViewModelProvider(this, shortsViewModelFactory).get(ShortsViewModel::class.java)

        if(NetworkUtilities.isNetworkAvailable(requireContext()).equals(true)){
            shortsViewModel.shorts.observe(viewLifecycleOwner){ shorts ->
                when(shorts){
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        if(shorts.data.isEmpty()){
                            binding.playWithShortsEffect.apply {
                                startShimmer()
                                visibility = View.VISIBLE
                            }
                        }else{
                            binding.playWithShortsEffect.apply {
                                stopShimmer()
                                visibility = View.GONE
                            }
                            binding.playWithShorts.apply {
                                adapter = ShortsAdapter(
                                    shorts.data
                                )
                            }
                        }
                    }
                    is YoutubeResource.Error -> {
                        binding.playWithShortsEffect.apply {
                            startShimmer()
                            visibility = View.VISIBLE
                        }
                    }
                }
            }
        }else{
           Utilities.turnOnNetworkDialog(requireContext(),"Shorts")
        }
    }
}