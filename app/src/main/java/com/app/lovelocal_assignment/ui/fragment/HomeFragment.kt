package com.app.lovelocal_assignment.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.app.lovelocal_assignment.R
import com.app.lovelocal_assignment.databinding.FragmentHomeBinding
import com.app.lovelocal_assignment.model.Product
import com.app.lovelocal_assignment.ui.adapter.ProductCategoryDataRvAdapter
import com.app.lovelocal_assignment.viewmodel.HomeViewModel
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private var categoryDataRvAdapter: ProductCategoryDataRvAdapter? = null

    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        binding.homeViewModel = homeViewModel

        initObserver()
        initAdapter()
        getGreetingMessage()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /***
     * This method is use to initialize observer
     */
    private fun initObserver() {
        homeViewModel.productDataListMutableData.observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {
                _binding?.tvNoDataFound?.visibility = View.GONE
                categoryDataRvAdapter?.clear()
                categoryDataRvAdapter?.addAll(it)
            } else {
                _binding?.tvNoDataFound?.visibility = View.VISIBLE
            }
        })
        homeViewModel.locationListMutableData.observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {

                Timber.e("Latitude: ${it[0].latitude}")
                Timber.e("Longitude: ${it[0].longitude}")
                Timber.e("Latitude: ${it[0].latitude.toInt()}")
                Timber.e("Longitude: ${it[0].longitude.toInt()}")
                Timber.e("Country Name: ${it[0].countryName}")
                Timber.e("Locality: ${it[0].locality}")
                Timber.e("Address: ${it[0].getAddressLine(0)}")

                homeViewModel.latitude.postValue(it[0].latitude.toInt())
                homeViewModel.longitude.postValue(it[0].longitude.toInt())

                _binding?.tvYourLocationAddress?.text = it[0].getAddressLine(0)
            }
        })
    }

    private fun initAdapter() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryList.layoutManager = gridLayoutManager

        categoryDataRvAdapter = ProductCategoryDataRvAdapter(requireContext())
        binding.rvCategoryList.adapter = categoryDataRvAdapter
        categoryDataRvAdapter?.setItemClickListener(object :
            ProductCategoryDataRvAdapter.OnItemClickListener {
            override fun onItemClick(data: Product, position: Int) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_navigation_home_to_navigation_dashboard)

                homeViewModel.selectedProduct.postValue(data)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Timber.e("onResume of HomeFragment")
        homeViewModel.productDataListMutableData.value?.let {
            categoryDataRvAdapter?.clear()
            categoryDataRvAdapter?.addAll(it)
        }
        getGreetingMessage()
    }

    private fun getGreetingMessage() {
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        var greeting = ""
        greeting = when (hour) {
            in 12..16 -> {
                "Good Afternoon";
            }
            in 17..20 -> {
                "Good Evening";
            }
            in 21..23 -> {
                "Good Night";
            }
            else -> {
                "Good Morning";
            }
        }
        homeViewModel.strGreetingMessage.postValue("$greeting John")
    }
}