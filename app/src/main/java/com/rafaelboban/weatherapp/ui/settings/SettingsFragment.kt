package com.rafaelboban.weatherapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.databinding.ClearDialogBinding
import com.rafaelboban.weatherapp.databinding.FragmentSettingsBinding
import com.rafaelboban.weatherapp.ui.viewmodels.SettingsViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.databinding.SnackbarBinding
import com.rafaelboban.weatherapp.ui.info.InfoActivity
import com.rafaelboban.weatherapp.ui.location.LocationActivity
import com.rafaelboban.weatherapp.ui.location.unit


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    var unit = "metric"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupViewModel()
        setupListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupLanguageMenu()
        setupCityMenu()
        getPreferences()

        if (unit == "imperial") {
            binding.radioImperial.isChecked = true
        } else {
            binding.radioMetric.isChecked = true
        }
    }

    private fun setupCityMenu() {
    }

    private fun setupLanguageMenu() {
        val languages = resources.getStringArray(R.array.languages)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            languages
        )
        binding.languageMenu.setAdapter(arrayAdapter)
        binding.languageMenu.setText(arrayAdapter.getItem(0).toString(), false)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.apiService),
                DbHelper(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(SettingsViewModel::class.java)
    }

    private fun setupListeners() {
        binding.clearCitiesButton.setOnClickListener {
            showDialog("FAVORITES")
        }

        binding.clearRecentButton.setOnClickListener {
            showDialog("RECENT")
        }

        binding.infoButton.setOnClickListener {
            val intent = Intent(context, InfoActivity::class.java)
            context?.startActivity(intent)
        }
        binding.groupUnits.setOnCheckedChangeListener { group, checkedId ->
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            if (sp.contains("initialized")) {
                val ed = sp.edit();
                if (checkedId == R.id.radio_imperial) {
                    Log.e("xxx", "imp")
                    ed.putString("unit", "imperial")
                    unit = "imperial"
                } else {
                    ed.putString("unit", "metric")
                    Log.e("xxx", "met")
                    unit = "metric"
                }
                ed.apply();
            }
        }
    }

    private fun getPreferences() {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        unit = sp.getString("unit", "metric")!!
    }

    private fun showDialog(type: String) {
        val dialogBinding = ClearDialogBinding.inflate(LayoutInflater.from(context))
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);

        when (type) {
            "FAVORITES" -> {
                dialogBinding.dialogTitle.text = getString(R.string.clear_my_cities_list_q)
                dialogBinding.warningText.text = getString(R.string.favorites_clear_warning)
            }
            "RECENT" -> {
                dialogBinding.dialogTitle.text = getString(R.string.clear_recent_search_list_q)
                dialogBinding.warningText.text = getString(R.string.recents_clear_warning)
            }
        }

        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT);
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0);
        val snackBinding = SnackbarBinding.inflate(LayoutInflater.from(context))

        snackBinding.snackbarClose.setOnClickListener {
            snackbar.dismiss()
        }

        dialogBinding.clearButton.setOnClickListener {
            when (type) {
                "FAVORITES" -> {
                    viewModel.clearFavorites()
                    snackBinding.message.text = getString(R.string.cities_is_clear)
                    snackbarLayout.addView(snackBinding.root, 0)
                    snackbar.show()
                }
                "RECENT" -> {
                    viewModel.clearRecent()
                    snackBinding.message.text = getString(R.string.recent_is_clear)
                    snackbarLayout.addView(snackBinding.root, 0)
                    snackbar.show()
                }
            }
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}