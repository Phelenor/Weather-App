package com.rafaelboban.weatherapp.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
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
    private lateinit var recyclerAdapterFavorites: LocationsAdapter

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                UP or
                        DOWN or
                        START or
                        END, 0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as LocationsAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        recyclerView = binding.favoritesRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerAdapterFavorites = LocationsAdapter(LinkedHashMap(), true, this)
        recyclerView.adapter = recyclerAdapterFavorites

        setupViewModel()
        setupObservers()
        setupListeners()
        viewModel.getLocations()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLocations()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.apiService),
                DbHelper(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(FavoritesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.weatherMap.observe(viewLifecycleOwner, {
            recyclerAdapterFavorites.apply {
                addWeathers(it)
                notifyDataSetChanged()
            }
        })
    }

    private fun setupListeners() {
        binding.toolbarFavorites.editButton.setOnClickListener {
            binding.toolbarFavorites.editButton.visibility = View.GONE
            binding.toolbarFavorites.calendar.visibility = View.GONE
            binding.toolbarFavorites.editDoneButton.visibility = View.VISIBLE
            recyclerAdapterFavorites.FAVORITES_EDIT_MODE = true
            itemTouchHelper.attachToRecyclerView(recyclerView)
            recyclerAdapterFavorites.notifyDataSetChanged()
        }

        binding.toolbarFavorites.editDoneButton.setOnClickListener {
            binding.toolbarFavorites.editDoneButton.visibility = View.GONE
            binding.toolbarFavorites.editButton.visibility = View.VISIBLE
            binding.toolbarFavorites.calendar.visibility = View.VISIBLE
            itemTouchHelper.attachToRecyclerView(null)
            recyclerAdapterFavorites.FAVORITES_EDIT_MODE = false
            recyclerAdapterFavorites.notifyDataSetChanged()

            viewModel.updateFavorites(recyclerAdapterFavorites.weatherMap.keys.toMutableList())
        }
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

}