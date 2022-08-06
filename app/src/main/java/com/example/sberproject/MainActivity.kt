package com.example.sberproject

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.sberproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        navView.setupWithNavController(navController)

        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbar)

        binding.accButton.setOnClickListener {
            navController.navigate(R.id.accountFragment)
        }
        binding.setButton.setOnClickListener {
            navController.navigate(R.id.navigation_setting)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun login() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.visibility = View.VISIBLE
        binding.accButton.visibility = View.VISIBLE
        binding.setButton.visibility = View.VISIBLE
    }

    override fun logout() {
        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE
    }
}