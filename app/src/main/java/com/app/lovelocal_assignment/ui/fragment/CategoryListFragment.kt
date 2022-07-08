package com.app.lovelocal_assignment.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.lovelocal_assignment.R
import com.app.lovelocal_assignment.databinding.FragmentCategoryListBinding
import com.app.lovelocal_assignment.model.CategoryData
import com.app.lovelocal_assignment.ui.adapter.CategoryDataRvAdapter
import com.app.lovelocal_assignment.viewmodel.HomeViewModel
import timber.log.Timber


class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private var categoryDataRvAdapter: CategoryDataRvAdapter? = null
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var _context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        binding.categoryListFragment = this

        _context = requireContext()

        initObserver()
        initAdapter()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.e("onAttach of CategoryListFragment")
        _context = context
    }

    /***
     * This method is use to initialize observer
     */
    private fun initObserver() {
        homeViewModel.selectedProduct.observe(requireActivity(), Observer {
            _binding?.tvHeaderName?.text = it.name
        })
        homeViewModel.categoryResponse.observe(requireActivity(), Observer {
            if (it.data.isNotEmpty()) {
                _binding?.tvNoDataFound?.visibility = View.GONE
                categoryDataRvAdapter?.addAll(it.data)
            } else {
                _binding?.tvNoDataFound?.visibility = View.VISIBLE
            }
        })
    }

    private fun initAdapter() {

        categoryDataRvAdapter = CategoryDataRvAdapter(requireContext())

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvSubCategoryList.layoutManager = linearLayoutManager

        val itemDecorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_divider
            )!!
        )
        binding.rvSubCategoryList.addItemDecoration(itemDecorator)

        binding.rvSubCategoryList.adapter = categoryDataRvAdapter
        categoryDataRvAdapter?.setItemClickListener(object :
            CategoryDataRvAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryData, position: Int) {
                // handle click event here
            }
        })
    }

    fun goBackClick(){
        activity?.onBackPressed()
    }
}