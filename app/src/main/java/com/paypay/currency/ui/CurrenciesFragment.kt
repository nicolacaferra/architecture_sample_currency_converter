package com.paypay.currency.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.paypay.currency.R
import com.paypay.currency.data.CurrencyValue
import com.paypay.currency.data.entities.Currency
import com.paypay.currency.databinding.FragmentCurrenciesBinding
import com.paypay.currency.ui.adapters.CurrenciesAdapter
import com.paypay.currency.ui.adapters.decoration.GridSpacerDecoration
import com.paypay.currency.ui.dialogs.Dialogs
import com.paypay.currency.ui.viewmodels.CurrenciesViewModel
import com.paypay.currency.ui.viewmodels.Status
import dagger.hilt.android.AndroidEntryPoint

/**
 * fragment responsable to show list of currencies
 */
@AndroidEntryPoint
class CurrenciesFragment : Fragment() {

    //view model
    private val viewModel: CurrenciesViewModel by viewModels()

    //adapter to show currency values
    private val adapter: CurrenciesAdapter = CurrenciesAdapter()

    //view binding
    private lateinit var binding: FragmentCurrenciesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentCurrenciesBinding.inflate(inflater)

        //observing currencies, to show inside dropdown list
        viewModel.currencies.observe(viewLifecycleOwner) { state ->

            when (state.status) {
                Status.SUCCESS -> {
                    setupDropdown(state.data as List<Currency>)
                }
                Status.LOADING -> {
                    showLoadingCurrencies()
                }
                Status.ERROR -> {
                    showErrorWithRetry()
                }
            }
        }

        //observing currency values, to show inside recycler view
        viewModel.currencyValues.observe(viewLifecycleOwner) { state ->
            when (state.status) {
                Status.SUCCESS -> {
                    showData(state.data)
                }
                Status.LOADING -> {
                    showLoadingValues()
                }
                Status.ERROR -> {
                    showError(state.message)
                }
            }
        }


        (binding.currenciesDropdown.editText as? AutoCompleteTextView)?.setOnItemClickListener { dropdown, _, position, _ ->
            val selectedCurrency = dropdown.adapter.getItem(position) as Currency
            viewModel.changeSelectedCurrency(selectedCurrency)
            binding.recyclerview.smoothScrollToPosition(0)
        }

        binding.currencyInput.editText?.doAfterTextChanged { text ->
            viewModel.changeAmount(text.toString().toDoubleOrNull())
        }


        binding.currencyInput.editText?.setText(viewModel.amountToConvert?.toString())
        binding.currenciesDropdown.editText?.setText(viewModel.selectedCurrency?.toString())

        setUpRecycler()

        return binding.root
    }

    private fun setupDropdown(data: List<Currency>) {
        binding.currenciesDropdown.visibility = View.VISIBLE
        binding.progressCurrencies.visibility = View.GONE
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            data
        )

        (binding.currenciesDropdown.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
    }


    private fun setUpRecycler() {
        val columns =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = GridLayoutManager(activity, columns)
        binding.recyclerview.addItemDecoration(
            GridSpacerDecoration(
                columns,
                resources.getDimensionPixelSize(R.dimen.adapter_item_space),
                false
            )
        )

        binding.recyclerview.adapter = adapter
    }


    private fun showLoadingCurrencies() {
        binding.currenciesDropdown.visibility = View.GONE
        binding.progressCurrencies.visibility = View.VISIBLE
    }

    private fun showLoadingValues() {
        binding.emptyListLabel.visibility = View.GONE
        binding.recyclerview.visibility = View.GONE
        binding.circularProgress.visibility = View.VISIBLE
    }

    private fun showErrorWithRetry() {
        Dialogs.showErrorDialogWithRetry(
            requireContext(),
            { viewModel.loadCurrencies() },
            { requireActivity().finish() })

        binding.currenciesDropdown.visibility = View.GONE
        binding.progressCurrencies.visibility = View.GONE

        binding.recyclerview.visibility = View.GONE
        binding.circularProgress.visibility = View.GONE


    }

    private fun showError(message: String?) {
        Dialogs.showSimpleErrorDialog(requireContext(), message)

        binding.currenciesDropdown.visibility = View.GONE
        binding.progressCurrencies.visibility = View.GONE

        binding.recyclerview.visibility = View.GONE
        binding.circularProgress.visibility = View.GONE
    }

    private fun showData(currenciesValue: List<CurrencyValue>?) {
        binding.circularProgress.visibility = View.GONE
        if (currenciesValue != null) {
            binding.emptyListLabel.visibility = View.GONE
            binding.recyclerview.visibility = View.VISIBLE
            adapter.setData(currenciesValue)
        } else {
            binding.emptyListLabel.visibility = View.VISIBLE
            binding.recyclerview.visibility = View.GONE
        }
    }

}