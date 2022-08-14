package com.example.sberproject.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.MainActivityCallback
import com.example.sberproject.R
//import com.example.sberproject.authentication.AuthenticationViewModel
import com.example.sberproject.databinding.FragmentAccountBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountViewModel() as T
            }
        }).get(AccountViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.logOutButton.setOnClickListener {
            viewModel.clickOnSignOutButton()
        }
        val user = Firebase.auth.currentUser
        user?.let {
            binding.email.text = "Email: ${it.email.toString()}"
            binding.nickname.text = "Nickname: ${it.email.toString().split('@')[0]}"
        }
        if (user == null) {
            binding.email.visibility = View.GONE
            binding.nickname.visibility = View.GONE
            binding.logOutButton.visibility = View.GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigateToAuthPage.observe(viewLifecycleOwner) {
            (activity as MainActivityCallback?)?.logout()
            findNavController().navigate(R.id.authenticationFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("Профиль")
    }
}