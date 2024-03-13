package com.google.android.piyush.dopamine.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.HomeAdapter
import com.google.android.piyush.dopamine.adapters.SearchAdapter
import com.google.android.piyush.dopamine.databinding.FragmentExperimentalSearchBinding
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.ExperimentalDefaultVideosViewModel
import com.google.android.piyush.youtube.viewModels.ExperimentalVideosViewModelFactory
import java.util.Locale


class ExperimentalSearch : Fragment() {
    private val TAG = "ExperimentalSearch"
    private var experimentalSearchBinding: FragmentExperimentalSearchBinding? = null
    private lateinit var viewModel : ExperimentalDefaultVideosViewModel
    private lateinit var repository : YoutubeRepositoryImpl
    private lateinit var viewModelFactory : ExperimentalVideosViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_experimental_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentExperimentalSearchBinding.bind(view)
        experimentalSearchBinding = binding
        repository = YoutubeRepositoryImpl()
        viewModelFactory = ExperimentalVideosViewModelFactory(repository)
        viewModel = viewModelFactory.create(ExperimentalDefaultVideosViewModel::class.java)

        /*
        binding.searchView.inflateMenu(R.menu.search_menu)
        binding.searchView.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search -> {
                    ToastUtilities.showToast(requireContext(),"Search")
                    true
                }
                else -> false
            }
        }
        */

        binding.searchBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.search -> {
                    voiceSearchUtility()
                    true
                }
                else -> false
            }
        }

        binding.searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                binding.searchBar.setText(binding.searchView.text)
                binding.searchView.hide()
                true
            }

        /*
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState === TransitionState.SHOWING) {
                ToastUtilities.showToast(requireContext(),"Showing")
            }
        }
        */

        viewModel.defaultVideos.observe(viewLifecycleOwner){ defaultVideos ->
            when(defaultVideos){
                is YoutubeResource.Loading -> {}
                is YoutubeResource.Success -> {
                    Log.d(TAG, "onViewCreated: ${defaultVideos.data}")
                    binding.searchData.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = HomeAdapter(context,defaultVideos.data)
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "onViewCreated: ${defaultVideos.exception}")
                }
            }
        }

        binding.searchView.editText.addTextChangedListener {
            viewModel.getSearchVideos(it.toString())
            viewModel.searchVideos.observe(viewLifecycleOwner){ searchVideos ->
                when(searchVideos){
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        Log.d(TAG, "onViewCreated: ${searchVideos.data}")
                        binding.searchResults.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = SearchAdapter(context,searchVideos.data)
                            }
                    }
                    is YoutubeResource.Error -> {
                        Log.d(TAG, "onViewCreated: ${searchVideos.exception}")
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun voiceSearchUtility() {
        if(
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                Utilities.REQUEST_CODE_SPEECH_INPUT
            )
        }else{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search 😊")
            startActivityForResult(intent, Utilities.REQUEST_CODE_SPEECH_INPUT)
        }
    }
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Utilities.REQUEST_CODE_SPEECH_INPUT){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search 😊")
                startActivityForResult(intent, Utilities.REQUEST_CODE_SPEECH_INPUT)
            }
        }
    }
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Utilities.REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            //experimentalSearchBinding!!.searchView.setQuery(result?.get(0), true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        experimentalSearchBinding = null
    }
}