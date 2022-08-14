package com.example.sberproject.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.MainActivityCallback
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentLoginBinding
import com.example.sberproject.databinding.FragmentRegisterBinding

class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel() as T
            }
        }).get(RegisterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View{
        Log.d("Login", "Login fragment create")
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            viewModel.clickOnSignUpButton()
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

        binding.passwordConfField.addTextChangedListener(object: FieldTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPasswordConfirm(s.toString())
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showMessage.observe(viewLifecycleOwner){
            val textView: TextView? = _binding?.errText
            textView?.visibility = TextView.VISIBLE
            textView?.text = it
        }
        viewModel.navigateToMainPage.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.navigation_articles)
            (activity as MainActivityCallback?)?.login()
        }
    }
}