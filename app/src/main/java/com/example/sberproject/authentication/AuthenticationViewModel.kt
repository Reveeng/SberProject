package com.example.sberproject.authentication

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberproject.R
import com.example.sberproject.ui.articles.ArticlesApi
import com.example.sberproject.ui.map.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class AuthViewModelAbstr : ViewModel(){
    protected var auth: FirebaseAuth = Firebase.auth
    private var email: String = ""
    private var password: String = ""

    protected val mutableNavigateToMainPage by lazy {
        MutableLiveData<Event<Int>>()
    }
    val navigateToMainPage: LiveData<Event<Int>> = mutableNavigateToMainPage

    protected val mutableShowMessage by lazy {
        MutableLiveData<String>()
    }
    val showMessage: LiveData<String> = mutableShowMessage

    fun setEmail(mail: String) {
        this.email = mail
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getEmail(): String {
        return this.email
    }

    fun getPassword(): String {
        return this.password
    }
}

class LoginViewModel : AuthViewModelAbstr(){
    init {
        val user = auth.currentUser
        if (user != null)
            mutableNavigateToMainPage.value = Event(0)
    }
    fun clickOnLoginButton() {

        if (getEmail().isBlank()) {
            mutableShowMessage.value = "Введите почту"
            return
        }
        if (getPassword().isBlank()) {
            mutableShowMessage.value = "Введите пароль"
            return
        }
        val a = auth.signInWithEmailAndPassword(getEmail(), getPassword())
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

class RegisterViewModel : AuthViewModelAbstr(){
    private var passwordConfirm: String = ""

    fun setPasswordConfirm(passwordConfirm: String) {
        this.passwordConfirm = passwordConfirm
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

    fun clickOnSignUpButton() {
        val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()
        if (!isValidEmail) {
            mutableShowMessage.value = "Почта введена неправильно"
            return
        }
        if (getPassword() != passwordConfirm) {
            mutableShowMessage.value = "Пароли не совпадают"
            return
        }
        if (!isStrongPassword(getPassword())) {
            mutableShowMessage.value =
                "Password must have length 8 or more and at least 1 uppercase letter and 1 lowercase letter"
            return
        }
        val a = auth.createUserWithEmailAndPassword(getEmail(), getPassword())
        a.addOnFailureListener {
            mutableShowMessage.value = it.message
        }
        a.addOnSuccessListener {
            mutableNavigateToMainPage.value = Event(0)
        }
    }
}

