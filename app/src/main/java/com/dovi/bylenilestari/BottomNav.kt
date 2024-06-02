package com.dovi.bylenilestari

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dovi.bylenilestari.Fragment.Categories
import com.dovi.bylenilestari.Fragment.Home
import com.dovi.bylenilestari.Fragment.Profile
import com.dovi.bylenilestari.Fragment.Watchlist
import com.dovi.bylenilestari.databinding.ActivityBottomNavBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.ArrayDeque
import java.util.Deque

class BottomNav : AppCompatActivity() {
    private lateinit var binding : ActivityBottomNavBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private val fragmentStack: Deque<Fragment> = ArrayDeque()
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.itemIconTintList = null

        // Inisialisasi tumpukan fragment dengan homeFragment
        if (fragmentStack.isEmpty()) {
            fragmentStack.push(Home())
        }

        loadFragment(fragmentStack.peek())
        bottomNavigationView.selectedItemId = getSelectedItemId(fragmentStack.peek())
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            val selectedFragment = getFragment(item.itemId)
            if (selectedFragment.javaClass != fragmentStack.peek().javaClass) {
                clearBackStack()
                fragmentStack.push(selectedFragment)
                loadFragment(selectedFragment)
            }
            true
        }
        requestPermission()
    }
    private fun clearBackStack() {
        fragmentStack.clear()
        fragmentStack.push(Home())
    }

    private fun getFragment(itemId: Int): Fragment {
        return when (itemId) {
            R.id.home -> Home()
            R.id.categories -> Categories()
            R.id.watchlist -> Watchlist()
            R.id.profile -> Profile()
            else -> Home()
        }
    }

    private fun getSelectedItemId(fragment: Fragment): Int {
        return when (fragment) {
            is Home -> R.id.home
            is Categories -> R.id.categories
            is Watchlist -> R.id.watchlist
            is Profile -> R.id.profile
            else -> R.id.home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        if (fragmentStack.peek() is Home) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Silakan tekan kembali sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            fragmentStack.pop()
            if (fragmentStack.isEmpty()) {
                finish()
            } else {
                loadFragment(fragmentStack.peek())
                bottomNavigationView.selectedItemId = getSelectedItemId(fragmentStack.peek())
            }
        }
    }

    private fun isCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun isLocationPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        var requestPermissionUser = mutableListOf<String>()
        if (!isLocationPermission()) {
            requestPermissionUser.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!isCameraPermission()) {
            requestPermissionUser.add(android.Manifest.permission.CAMERA)
        }
        if (requestPermissionUser.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, requestPermissionUser.toTypedArray(), 0)
        }
    }
}