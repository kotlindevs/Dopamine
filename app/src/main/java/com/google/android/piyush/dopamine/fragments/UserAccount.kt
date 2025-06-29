package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.database.entities.User
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.RecentlyExploredAdapter
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
                    }
                    true
                }
                R.id.settings -> {
                    true
                }
                else -> false
            }
        }

        database.getRecentWatchHistory.observe(viewLifecycleOwner) { videos ->
            binding?.apply {
                recentlyExplored.apply {
                    visibility = View.VISIBLE
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = RecentlyExploredAdapter(
                        videos = videos
                    )
                }
            }
        }

        database.getUser.observe(viewLifecycleOwner) { user ->
            if(user == null){
                binding?.apply {
                    userImage.visibility = View.VISIBLE
                    editUserImage.visibility = View.VISIBLE
                    userNameInputLayout.visibility = View.VISIBLE
                    userDescriptionLayout.visibility = View.VISIBLE
                    applyChanges.visibility = View.VISIBLE
                }
            }
            userView(user = user)
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

            CoroutineScope(Dispatchers.Main).launch{
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
                    if(name.isNotEmpty()){
                        database.setUser(user = user)
                        binding?.apply {
                            progressBar.visibility = View.GONE
                            userProfileImage.visibility = View.VISIBLE
                            userProfileName.visibility = View.VISIBLE
                            userProfileDescription.visibility = View.VISIBLE
                            userView(user = user)
                        }
                    }
                }
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