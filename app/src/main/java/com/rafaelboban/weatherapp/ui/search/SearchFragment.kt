package com.rafaelboban.weatherapp.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.data.adapters.LocationsAdapter
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var _binding: FragmentSearchBinding
    private val binding get() = _binding
    private lateinit var viewModel: SearchViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: LocationsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        recyclerView = binding.searchRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerAdapter = LocationsAdapter(arrayListOf(), arrayListOf())
        recyclerView.adapter = recyclerAdapter

        setupViewModel()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SearchViewModel::class.java)
    }

    private fun setupObservers() {

        viewModel.weathers.observe(viewLifecycleOwner, {
            recyclerAdapter.apply {
                addWeathers(it)
                notifyDataSetChanged()
            }
        })

        viewModel.locations.observe(viewLifecycleOwner, {
            recyclerAdapter.apply {
                Log.d("KEKW", it.toString())
                addLocations(it)
                notifyDataSetChanged()
            }
        })

    }

    private fun setupListeners() {
        binding.toolbarSearch.searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 1) {
                    viewModel.getLocations(s.toString())
                }
            }
        })
    }
}