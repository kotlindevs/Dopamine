package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.database.entities.User
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.FragmentUserAccountBinding
import dagger.hilt.android.AndroidEntryPoint

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

        val image = "https://i.giphy.com/VbnUQpnihPSIgIXuZv.webp"
        binding = FragmentUserAccountBinding.bind(view)
        Glide.with(requireContext())
            .load(image)
            .into(binding?.userImage!!)
        database.getUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                val name = user.userName
                val description = user.userDescription
                binding?.apply {
                    userNameInput.setText(name)
                    userDescription.setText(description)
                }
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

            val user = User(
                userName = name,
                userDescription = description
            )
            if(name.isNotEmpty()){
                database.setUser(user = user)
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