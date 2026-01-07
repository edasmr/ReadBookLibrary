package com.example.readbooklibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.readbooklibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, HomeFragment())
            addToBackStack(null)
            commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    changeFragment(HomeFragment())
                    binding.bottomNavigationView.removeBadge(R.id.home)
                }
                R.id.favorites -> changeFragment(FavoriteFragment())
                R.id.profile -> changeFragment(ProfileFragment())
            }
            true
        }

        binding.bottomNavigationView.getOrCreateBadge(R.id.home).apply {
            this.isVisible = true
            number = 99
        }
    }

    private  fun changeFragment(fragment : androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            addToBackStack(null)
            commit()
        }
    }
}