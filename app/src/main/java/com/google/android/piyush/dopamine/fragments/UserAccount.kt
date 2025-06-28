package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.FragmentUserAccountBinding

class UserAccount : Fragment() {

    private var binding : FragmentUserAccountBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserAccountBinding.bind(view)

        binding?.editUserImage?.setOnClickListener {
            launcher.launch("image/*")
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