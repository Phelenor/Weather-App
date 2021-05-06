package com.rafaelboban.weatherapp.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.ui.adapters.LocationsAdapter
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.databinding.FragmentFavoritesBinding
import com.rafaelboban.weatherapp.ui.viewmodels.FavoritesViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: LocationsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        recyclerView = binding.favoritesRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerAdapter = LocationsAdapter(LinkedHashMap(), true)
        recyclerView.adapter = recyclerAdapter

        setupViewModel()
        setupObservers()
        viewModel.getLocations()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), DbHelper(DatabaseBuilder.getInstance(requireContext())))
        ).get(FavoritesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.weatherMap.observe(viewLifecycleOwner, {
            recyclerAdapter.apply {
                addWeathers(it)
                notifyDataSetChanged()
            }
        })
    }
}