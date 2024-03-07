package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DopamineHomeViewModel : ViewModel() {

    private val _selectedFragment : MutableLiveData<Int> = MutableLiveData()
    val selectedFragment : MutableLiveData<Int> = _selectedFragment

    fun setSelectedFragment(fragment : Int) {
        _selectedFragment.value = fragment
    }
}