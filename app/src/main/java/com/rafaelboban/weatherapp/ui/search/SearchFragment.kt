package com.rafaelboban.weatherapp.ui.search

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.databinding.FragmentSearchBinding
import com.rafaelboban.weatherapp.databinding.SnackbarBinding
import com.rafaelboban.weatherapp.ui.adapters.LocationsAdapter
import com.rafaelboban.weatherapp.ui.viewmodels.SearchViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapterSearch: LocationsAdapter

    var recent_flag = true
    var last_query = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        recyclerView = binding.searchRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerAdapterSearch = LocationsAdapter(LinkedHashMap())
        recyclerView.adapter = recyclerAdapterSearch

        setupViewModel()
        setupObservers()
        setupListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (recent_flag) {
            binding.toolbarSearch.searchField.setText(":")
            binding.toolbarSearch.searchField.setText("")
        } else {
            binding.toolbarSearch.searchField.setText("")
            binding.toolbarSearch.searchField.setText(last_query)
            // binding.toolbarSearch.searchField.setSelection(last_query.length)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), DbHelper(DatabaseBuilder.getInstance(requireContext())))
        ).get(SearchViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.weatherMap.observe(viewLifecycleOwner, {
            recyclerAdapterSearch.apply {
                addWeathers(it)
                notifyDataSetChanged()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.TRANSPARENT);
            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0);
            val snackBinding = SnackbarBinding.inflate(LayoutInflater.from(context))
            if (!it) {
                snackBinding.snackbarClose.setOnClickListener {
                    snackbar.dismiss()
                }
                snackBinding.message.text = getString(R.string.network_error)
                snackBinding.message.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.error))
                snackbarLayout.addView(snackBinding.root, 0)
                snackbar.show()
            }
        })
    }

    private fun setupListeners() {
        binding.toolbarSearch.searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 0) {
                    binding.toolbarSearch.iconClose.visibility = View.VISIBLE
                } else {
                    binding.toolbarSearch.iconClose.visibility = View.GONE
                }

                if (s.length >= 2) {
                    binding.textRecent.text = getString(R.string.results_tv)
                    viewModel.cancelOps()
                    recent_flag = false
                    last_query = s.toString()
                    viewModel.getLocations(s.toString())
                } else if (s.length <= 1) {
                    binding.textRecent.text = getString(R.string.recent_tv)
                    viewModel.cancelOps()
                    viewModel.clearData()
                    recent_flag = true
                    viewModel.getLocations(recent = true)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.toolbarSearch.iconClose.setOnClickListener {
            binding.toolbarSearch.searchField.setText("")
        }
    }
}
