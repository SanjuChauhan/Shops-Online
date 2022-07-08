package com.app.lovelocal_assignment.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.lovelocal_assignment.R
import com.app.lovelocal_assignment.databinding.ActivityMainBinding
import com.app.lovelocal_assignment.model.Product
import com.app.lovelocal_assignment.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.*


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        hideSystemUI(window)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initObserver()
        loadCategoryData()

        getLocation()
    }

    /***
     * This method is use to initialize observer
     */
    private fun initObserver() {
        homeViewModel.strToastMessage.observe(
            this,
            Observer { message -> showToast(message) })
        homeViewModel.isShowProgressDialog.observe(this, Observer {
            if (it) {
                showProgressDialog("Please Wait!")
            } else {
                dismissProgressDialog()
            }
        })
        homeViewModel.selectedProduct.observe(this, Observer {
            getCategoryList(it.id)
        })
        homeViewModel.strSearch.observe(this, Observer { str ->
            Timber.e("Search %s", str)
            if (str.length >= 3) {
                getSearchedProductsAPI(str)
            } else {
                loadCategoryData()
            }
        })
    }

    /**
     * This method is use for loading local data.
     */
    private fun loadCategoryData() {
        val categoryList: ArrayList<Product> = arrayListOf()

        categoryList.add(
            Product(
                7814,
                "Fruits & Vegetables",
                getLocalDrawableURL(R.drawable.img_fruits_vegetables)
            )
        )
        categoryList.add(
            Product(
                7828,
                "Meat & Sea Food",
                getLocalDrawableURL(R.drawable.img_meat_seafood)
            )
        )
        categoryList.add(
            Product(
                7827,
                "Health & Medicines",
                getLocalDrawableURL(R.drawable.img_health_medicine)
            )
        )
        categoryList.add(Product(7818, "Dairy", getLocalDrawableURL(R.drawable.img_dairy)))
        categoryList.add(
            Product(
                7915,
                "Chocolate & Snacks",
                getLocalDrawableURL(R.drawable.img_chocolates_snacks)
            )
        )
        categoryList.add(
            Product(
                7821,
                "Personal Care",
                getLocalDrawableURL(R.drawable.img_personal_care)
            )
        )

        homeViewModel.productDataListMutableData.postValue(categoryList)
    }

    private fun getLocalDrawableURL(id: Int): String {
        return "android.resource://" + baseContext.packageName + "/" + id
    }

    /**
     * Get Category List
     */
    private fun getCategoryList(categoryId: Int) {
        if (checkNetworkState()) {
            homeViewModel.callCategoryListAPI(categoryId)
        } else {
            homeViewModel.strToastMessage.postValue(getString(R.string.msg_no_internet))
        }
    }

    /**
     * Get Category List
     */
    private fun getSearchedProductsAPI(strSearch: String) {
        if (checkNetworkState()) {
            homeViewModel.callSearchedProductsAPI(strSearch)
        } else {
            homeViewModel.strToastMessage.postValue(getString(R.string.msg_no_internet))
        }
    }

    private fun hideSystemUI(window: Window) {
        //pass getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.systemBars())
        } else {
            val decorView: View = window.decorView
            var uiVisibility: Int = decorView.getSystemUiVisibility()
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            decorView.setSystemUiVisibility(uiVisibility)
        }
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        homeViewModel.locationListMutableData.postValue(list)
                    }
                }
            } else {
                showToast("Please turn on location")
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}