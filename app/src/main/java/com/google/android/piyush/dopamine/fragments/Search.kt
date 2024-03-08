package com.google.android.piyush.dopamine.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.piyush.database.entities.EntityVideoSearch
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineUserProfile
import com.google.android.piyush.dopamine.adapters.SearchAdapter
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.databinding.FragmentSearchBinding
import com.google.android.piyush.dopamine.viewModels.SearchViewModel
import com.google.android.piyush.dopamine.viewModels.SearchViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class Search : Fragment() {
    private var fragmentSearchBinding: FragmentSearchBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var searchViewModelFactory: SearchViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
        fragmentSearchBinding = binding
        firebaseAuth = FirebaseAuth.getInstance()
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        searchViewModelFactory = SearchViewModelFactory(youtubeRepositoryImpl)
        searchViewModel = ViewModelProvider(this, searchViewModelFactory).get(SearchViewModel::class.java)
        databaseViewModel = DatabaseViewModel(context?.applicationContext!!)

        if(firebaseAuth.currentUser?.email.toString().isEmpty()){
            Glide.with(this).load(SignInUtils.DEFAULT_IMAGE).into(fragmentSearchBinding!!.userImage)
        }else{
            Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(fragmentSearchBinding!!.userImage)
        }

        fragmentSearchBinding!!.userImage.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    DopamineUserProfile::class.java
                )
            )
        }

        binding.clearAll.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                databaseViewModel.deleteSearchVideoList()
            }
            binding.utilList.visibility = View.GONE
            binding.searchEffect.visibility = View.VISIBLE
            binding.clearAll.visibility = View.GONE
        }

        val searchVideoList = databaseViewModel.getSearchVideoList()

        binding.searchVideo.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.utilList.visibility = View.VISIBLE
                    binding.searchEffect.visibility = View.INVISIBLE
                    databaseViewModel.insertSearchVideos(
                        EntityVideoSearch(
                            Random.nextInt(1, 100000),
                            query
                        )
                    )
                    Toast.makeText(context, "Insert Query", Toast.LENGTH_SHORT).show()

                    searchViewModel.searchVideos(query!!)

                    searchViewModel.searchVideos.observe(viewLifecycleOwner){ searchVideos ->
                        when(searchVideos){
                            is YoutubeResource.Loading -> {
                                Log.d(TAG, "onQueryTextSubmit: Loading")
                                binding.utilList.visibility = View.GONE
                            }
                            is YoutubeResource.Success -> {
                                binding.utilList.apply{
                                    layoutManager = LinearLayoutManager(context)
                                    visibility = View.VISIBLE
                                    adapter = SearchAdapter(context!!, searchVideos.data)
                                }
                                //Log.d(TAG, "onQueryTextSubmit: ${ searchVideos.data}")
                            }
                            is YoutubeResource.Error -> {
                                Log.d(TAG, "onQueryTextSubmit: ${searchVideos.exception}")
                            }
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }
        )

        binding.voiceSearch.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search video here 🫡")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSearchBinding = null
    }

}