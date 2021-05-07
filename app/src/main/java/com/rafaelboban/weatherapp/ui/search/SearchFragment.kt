package com.rafaelboban.weatherapp.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.databinding.FragmentSearchBinding
import com.rafaelboban.weatherapp.ui.adapters.LocationsAdapter
import com.rafaelboban.weatherapp.ui.viewmodels.SearchViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: LocationsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        recyclerView = binding.searchRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerAdapter = LocationsAdapter(LinkedHashMap())
        recyclerView.adapter = recyclerAdapter

        setupViewModel()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), DbHelper(DatabaseBuilder.getInstance(requireContext())))
        ).get(SearchViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.weatherMap.observe(viewLifecycleOwner, {
            recyclerAdapter.apply {
                addWeathers(it)
                notifyDataSetChanged()
            }
        })
    }

    private fun setupListeners() {
        binding.toolbarSearch.searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! >= 2) {
                    binding.textRecent.text = "Results"
                    viewModel.cancelOps()
                    viewModel.getLocations(s.toString())
                } else if (s.length <= 1) {
                    binding.textRecent.text = "Recent"
                    viewModel.cancelOps()
                    viewModel.clearData()
                    viewModel.getLocations(recent = true)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}
