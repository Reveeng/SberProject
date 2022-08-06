package com.example.sberproject.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.ui.map.Event
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountViewModel: ViewModel() {
    private val mutableNavigateToAuthPage by lazy{
        MutableLiveData<Event<Int>>()
    }
    val navigateToAuthPage: LiveData<Event<Int>> = mutableNavigateToAuthPage

    fun clickOnSignOutButton(){
        Firebase.auth.signOut()
        mutableNavigateToAuthPage.value = Event(0)
    }
}