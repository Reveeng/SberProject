package com.example.sberproject.authentication

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.ui.map.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private var authenticationState: AuthenticationState = AuthenticationState.REGISTRATION
    private var email: String = ""
    private var password: String = ""
    private var passwordConfirm: String = ""

    private val mutableNavigateToMainPage by lazy {
        MutableLiveData<Event<Int>>()
    }
    val navigateToMainPage: LiveData<Event<Int>> = mutableNavigateToMainPage

    private val mutableNavigateToLogin by lazy {
        MutableLiveData<Event<Int>>()
    }
    val navigateToLogin: LiveData<Event<Int>> = mutableNavigateToLogin

    private val mutableNavigateToRegistration by lazy {
        MutableLiveData<Event<Int>>()
    }
    val navigateToRegistration: LiveData<Event<Int>> = mutableNavigateToRegistration

    private val mutableShowMessage by lazy {
        MutableLiveData<String>()
    }
    val showMessage: LiveData<String> = mutableShowMessage

    init {
        val user = auth.currentUser
        if (user != null)
            mutableNavigateToMainPage.value = Event(0)
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun setPasswordConfirm(passwordConfirm: String) {
        this.passwordConfirm = passwordConfirm
    }

    fun clickOnSignUpButton() {
        if (authenticationState == AuthenticationState.LOGIN) {
            authenticationState = AuthenticationState.REGISTRATION
            mutableNavigateToRegistration.value = Event(0)
            return
        }
        val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isValidEmail) {
            mutableShowMessage.value = "Not valid email"
            return
        }
        if (password != passwordConfirm) {
            mutableShowMessage.value = "Passwords must match"
            return
        }
        if (!isStrongPassword(password)) {
            mutableShowMessage.value =
                "Password must have length 8 or more and at least 1 uppercase letter and 1 lowercase letter"
            return
        }
        val a = auth.createUserWithEmailAndPassword(email, password)
        a.addOnFailureListener {
            mutableShowMessage.value = it.message
        }
        a.addOnSuccessListener {
            mutableNavigateToMainPage.value = Event(0)
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        if (password.length < 8)
            return false
        var hasUppercaseLetter = false
        var hasLowercaseLetter = false
        var hasNumber = false
        password.forEach { ch ->
            if (ch.isDigit())
                hasNumber = true
            else if (ch.isLowerCase())
                hasLowercaseLetter = true
            else if (ch.isUpperCase())
                hasUppercaseLetter = true
            if (hasNumber && hasLowercaseLetter && hasUppercaseLetter)
                return true
        }
        return false
    }

    fun clickOnLoginButton() {
        if (authenticationState == AuthenticationState.REGISTRATION) {
            authenticationState = AuthenticationState.LOGIN
            mutableNavigateToLogin.value = Event(0)
            return
        }
        if (email.isBlank()) {
            mutableShowMessage.value = "Enter email"
            return
        }
        if (password.isBlank()) {
            mutableShowMessage.value = "Enter password"
            return
        }
        val a = auth.signInWithEmailAndPassword(email, password)
        a.addOnFailureListener {
            mutableShowMessage.value = it.message
        }
        a.addOnSuccessListener {
            mutableNavigateToMainPage.value = Event(0)
        }
    }

    fun clickOnContinueButton() {
        mutableNavigateToMainPage.value = Event(0)
    }
}

