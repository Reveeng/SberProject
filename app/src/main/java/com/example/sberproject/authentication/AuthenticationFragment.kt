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
import androidx.viewpager2.widget.ViewPager2
import com.example.sberproject.MainActivityCallback
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentAuthenticationBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class AuthenticationFragment : Fragment() {
    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewpager: ViewPager2
    private lateinit var  tablayout: TabLayout

    private lateinit var loginAdapter: LoginAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
//        binding.signUpButton.setOnClickListener {
//            viewModel.clickOnSignUpButton()
//        }

        /*binding.confirmPasswordField.addTextChangedListener(object : FieldTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPasswordConfirm(s.toString())
            }
        })*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginAdapter = LoginAdapter(requireActivity().supportFragmentManager, lifecycle)
        tablayout = _binding!!.authTabs
        viewpager = _binding!!.veiwPager
        viewpager.adapter = loginAdapter

        TabLayoutMediator(tablayout, viewpager){tab, position->
            when (position){
                0 -> tab.text = "Вход"
                else -> tab.text = "Регистрация"
            }
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("")
    }
}