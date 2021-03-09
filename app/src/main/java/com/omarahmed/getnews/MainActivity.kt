package com.omarahmed.getnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.omarahmed.getnews.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_GetNews)
        setContentView(binding.root)

        navController = findNavController(R.id.host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.detailsFragment -> binding.bottomNavigationView.isVisible = false
                R.id.searchFragment -> binding.bottomNavigationView.isVisible = false
                else -> binding.bottomNavigationView.isVisible = true
            }
        }
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.homeFragment,
//                R.id.exploreFragment,
//                R.id.savedFragment
//            )
//        )
//
//        binding.bottomNavigationView.setupWithNavController(navController)
//        setupActionBarWithNavController(navController,appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}