package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.database.entities.User
import com.google.android.piyush.database.entities.UserPlaylists
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.RecentlyExploredAdapter
import com.google.android.piyush.dopamine.adapters.UserPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.CreateUserPlaylistsBinding
import com.google.android.piyush.dopamine.databinding.FragmentUserAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccount : Fragment() {

    private var binding : FragmentUserAccountBinding? = null
    private val database : DopamineDbViewModel by viewModels<DopamineDbViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserAccountBinding.bind(view)

        database.getUser.observe(viewLifecycleOwner) { user ->
            if(user != null){
                binding?.apply {
                    userImage.visibility = View.GONE
                    editUserImage.visibility = View.GONE
                    userNameInputLayout.visibility = View.GONE
                    userDescriptionLayout.visibility = View.GONE
                    applyChanges.visibility = View.GONE
                    userProfileImage.visibility = View.VISIBLE
                    userProfileName.visibility = View.VISIBLE
                    userProfileDescription.visibility = View.VISIBLE
                    createUserPlaylist.visibility = View.VISIBLE
                }
            }else{
                binding?.apply {
                    userImage.visibility = View.VISIBLE
                    editUserImage.visibility = View.VISIBLE
                    userNameInputLayout.visibility = View.VISIBLE
                    userDescriptionLayout.visibility = View.VISIBLE
                    applyChanges.visibility = View.VISIBLE
                    userProfileImage.visibility = View.GONE
                    userProfileName.visibility = View.GONE
                    userProfileDescription.visibility = View.GONE
                    recentlyExploredTitle.visibility = View.GONE
                    recentlyExplored.visibility = View.GONE
                    userPlaylists.visibility = View.GONE
                    userPlaylistsTitle.visibility = View.GONE
                    createUserPlaylist.visibility = View.GONE
                }
            }
            userView(user = user)
        }

        binding?.createUserPlaylist?.setOnClickListener {
            val playlist = UserPlaylists()
            playlist.show(childFragmentManager, "BottomSheet")
        }

        binding?.toolBar?.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.editAccount -> {
                    binding?.apply {
                        userImage.visibility = View.VISIBLE
                        editUserImage.visibility = View.VISIBLE
                        userNameInputLayout.visibility = View.VISIBLE
                        userDescriptionLayout.visibility = View.VISIBLE
                        applyChanges.visibility = View.VISIBLE
                        userProfileImage.visibility = View.GONE
                        userProfileName.visibility = View.GONE
                        userProfileDescription.visibility = View.GONE
                        recentlyExploredTitle.visibility = View.GONE
                        recentlyExplored.visibility = View.GONE
                        userPlaylists.visibility = View.GONE
                        userPlaylistsTitle.visibility = View.GONE
                        createUserPlaylist.visibility = View.GONE
                    }
                    true
                }
                R.id.settings -> {
                    true
                }
                else -> false
            }
        }

        binding?.editUserImage?.setOnClickListener {
            Snackbar.make(
                view,
                "Coming Soon",
                Snackbar.LENGTH_SHORT
            ).apply {
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(R.string.dismiss){
                    dismiss()
                }
            }.show()
        }

        binding?.applyChanges?.setOnClickListener {
            val name = binding?.userNameInput?.text.toString()
            val description = binding?.userDescription?.text.toString()

            if(name.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding?.apply {
                        userImage.visibility = View.GONE
                        editUserImage.visibility = View.GONE
                        userNameInputLayout.visibility = View.GONE
                        userDescriptionLayout.visibility = View.GONE
                        applyChanges.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }

                    delay(2025).run {
                        val user = User(
                            userName = name,
                            userDescription = description
                        )
                        database.setUser(user = user)
                        binding?.apply {
                            progressBar.visibility = View.GONE
                            userProfileImage.visibility = View.VISIBLE
                            userProfileName.visibility = View.VISIBLE
                            userProfileDescription.visibility = View.VISIBLE
                            createUserPlaylist.visibility = View.VISIBLE
                            userView(user = user)
                        }
                    }
                }
            }else {
                Snackbar.make(view, "Nickname cannot be empty.", Snackbar.LENGTH_SHORT).apply {
                    addCallback(object : Snackbar.Callback() {
                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                            binding?.applyChanges?.isEnabled = false
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            dismiss()
                            binding?.applyChanges?.isEnabled = true
                        }
                    })
                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    setAction(R.string.dismiss) {
                        dismiss()
                    }
                }.show()
            }
        }
    }

    private fun userView(user: User?) {
        user?.let {
            val name = user.userName
            val description = user.userDescription
            binding?.apply {
                userProfileName.apply {
                    visibility = View.VISIBLE
                    text = name
                }
                userProfileDescription.apply {
                    visibility = View.VISIBLE
                    text = description
                }
                userProfileImage.visibility = View.VISIBLE
                userNameInput.setText(name)
                userDescription.setText(description)
            }
            database.apply {
                getRecentWatchHistory.observe(viewLifecycleOwner) { videos ->
                    videos?.count()?.let { i ->
                        if (i > 0) {
                            binding?.apply {
                                recentlyExploredTitle.visibility = View.VISIBLE
                                recentlyExplored.apply {
                                    visibility = View.VISIBLE
                                    layoutManager =
                                        LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                    adapter = RecentlyExploredAdapter(
                                        videos = videos
                                    )
                                }
                            }
                        } else {
                            binding?.apply {
                                recentlyExploredTitle.visibility = View.GONE
                                recentlyExplored.visibility = View.GONE
                            }
                        }
                    }

                    userPlaylists.observe(viewLifecycleOwner) { playlists ->
                        playlists?.count()?.let { i ->
                            if(i > 0) {
                                binding?.apply {
                                    userPlaylistsTitle.visibility = View.VISIBLE
                                    userPlaylists.apply {
                                        visibility = View.VISIBLE
                                        layoutManager = LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                        adapter = UserPlaylistsAdapter(
                                            playlists = playlists
                                        )
                                    }
                                }
                            }else{
                                binding?.apply {
                                    userPlaylistsTitle.visibility = View.GONE
                                    userPlaylists.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
        uri -> uri?.let {}
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

class UserPlaylists : BottomSheetDialogFragment(){

    private var binding : CreateUserPlaylistsBinding? = null
    private val database : DopamineDbViewModel by activityViewModels<DopamineDbViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_user_playlists,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = CreateUserPlaylistsBinding.bind(view)

        binding?.apply {
            playlistsNameLayout.visibility = View.VISIBLE
            playlistsDescriptionLayout.visibility = View.VISIBLE
            createPlaylist.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            progressBarViewer.visibility = View.GONE
        }

        binding?.createPlaylist?.setOnClickListener {
            val playListName = binding?.playlistsName?.text.toString()
            val description = binding?.playlistsDescription?.text.toString()

            if(!playListName.isEmpty()){
                lifecycleScope.launch {
                    binding?.apply {
                        playlistsNameLayout.visibility = View.GONE
                        playlistsDescriptionLayout.visibility = View.GONE
                        createPlaylist.visibility = View.GONE
                        progressBarViewer.visibility = View.VISIBLE
                        progressBar.visibility = View.VISIBLE
                    }
                    delay(2025).run {
                        database.createUserPlaylist(
                            playlists = UserPlaylists(
                                playlistName = playListName,
                                playlistDescription = description
                            )
                        )
                        dismiss()
                    }
                }
            }else{
                Snackbar.make(
                    view,
                    "Playlist name cannot be empty.",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    addCallback(
                        object : Snackbar.Callback(){
                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                binding?.createPlaylist?.isEnabled = false
                            }
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                dismiss()
                                binding?.createPlaylist?.isEnabled = true
                            }
                        }
                    )
                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    setAction(R.string.dismiss){
                        dismiss()
                    }
                }.show()
            }
        }

        binding?.close?.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}