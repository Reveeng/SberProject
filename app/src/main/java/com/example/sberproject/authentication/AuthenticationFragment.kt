package com.example.sberproject.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.MainActivityCallback
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentAuthenticationBinding
import com.google.firebase.auth.FirebaseAuth

class AuthenticationFragment : Fragment() {
    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthenticationViewModel() as T
            }
        }).get(AuthenticationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        binding.signUpButton.setOnClickListener {
            viewModel.clickOnSignUpButton()
        }
        binding.loginButton.setOnClickListener {
            viewModel.clickOnLoginButton()
        }
        binding.continueButton.setOnClickListener {
            viewModel.clickOnContinueButton()
        }
        binding.emailField.addTextChangedListener(object : FieldTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setEmail(s.toString())
            }
        })
        binding.passwordField.addTextChangedListener(object : FieldTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPassword(s.toString())
            }
        })
        binding.confirmPasswordField.addTextChangedListener(object : FieldTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPasswordConfirm(s.toString())
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showMessage.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.navigateToMainPage.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.navigation_articles)
            (activity as MainActivityCallback?)?.login()
        }
        viewModel.navigateToLogin.observe(viewLifecycleOwner) {
            binding.confirmPasswordField.visibility = View.GONE
            (activity as MainActivityCallback?)?.setActionBarTitle("Вход")
        }
        viewModel.navigateToRegistration.observe(viewLifecycleOwner) {
            binding.confirmPasswordField.visibility = View.VISIBLE
            (activity as MainActivityCallback?)?.setActionBarTitle("Регистрация")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("Регистрация")
    }
}