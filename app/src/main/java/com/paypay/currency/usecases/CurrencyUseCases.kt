package com.paypay.currency.usecases

import android.util.Log
import com.paypay.currency.BuildConfig
import com.paypay.currency.data.CurrencyValue
import com.paypay.currency.data.entities.Currency
import com.paypay.currency.data.entities.ExchangeRate
import com.paypay.currency.data.exceptions.GenericException
import com.paypay.currency.data.exceptions.NetworkException
import com.paypay.currency.data.repositories.HttpCurrencyRepository
import com.paypay.currency.data.repositories.LocalCurrencyRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * use case class used to handle application logic
 */
@Singleton
class CurrencyUseCases @Inject constructor(
    private val httpRepository: HttpCurrencyRepository,
    private val localRepository: LocalCurrencyRepository
) {

    private val tag = "useCase"

    /**
     * load all currencies from API and then store them inside local database for future uses
     */
    @Throws(NetworkException::class, GenericException::class)
    fun getAllCurrencies(): List<Currency> {
        try {
            //loading for local repository
            var currencies = localRepository.getCurrencies()

            //if empty, fetching from API
            if (currencies.isEmpty()) {

                val currenciesMap = httpRepository.fetchAllCurrencies()

                //mapping DTO received to a list of Currency entity class
                currencies = currenciesMap.map { entry ->
                    Currency(
                        entry.key,
                        entry.value,
                    )
                }

                //save fetched currencies to local DB
                localRepository.saveCurrencies(currencies)
            }

            return currencies
        } catch (networkException: NetworkException) {
            // rethrow the network exception, we already handled it
            Log.d(tag, "a network exception occurred, rethrowing it")
            throw networkException
        } catch (exception: Exception) {
            Log.w(tag, "an exception occurred: ${exception.message}", exception)
            //throw a custom generic exception to handle all others unexpected errors
            throw GenericException()
        }
    }

    /**
     * get currency values with amount and selected currency
     */
    @Throws(NetworkException::class, GenericException::class)
    fun getRatesByCurrencyAndAmount(
        selectedCurrency: Currency, amount: Double
    ): List<CurrencyValue> {

        try {

            val rates = getExchangeRates()
            val selectedRate =
                rates.find { rate -> rate.symbol == selectedCurrency.symbol }

            val adjustedRate = 1 / selectedRate!!.value

            val filteredRates =
                rates.filter { rate -> rate.symbol != selectedRate.symbol }

            val currenciesValue = filteredRates.map { rate ->
                CurrencyValue(
                    rate.symbol, (adjustedRate * rate.value * amount)
                )
            }

            return currenciesValue
        } catch (networkException: NetworkException) {
            // rethrow the network exception, we already handled it
            Log.d(tag, "a network exception occurred, rethrowing it")
            throw networkException
        } catch (exception: Exception) {
            Log.w(tag, "an exception occurred: ${exception.message}", exception)
            //throw a custom generic exception to handle all others unexpected errors
            throw GenericException()
        }
    }


    /**
     * loading exchange rates, refreshed each 30 mins
     */
    private fun getExchangeRates(): List<ExchangeRate> {


        var ratesList = localRepository.getRatesWithTimeGreaterThan(validTime)
        Log.d(
            tag,
            "rates have been loaded from local db, size: ${ratesList.size}, max valid time was : $validTime"
        )
        if (ratesList.isEmpty()) {

            Log.d(tag, "rates are empty or expired, need to fetch from API")
            val rates =
                httpRepository.fetchLatestExchangeRates(BuildConfig.OPENEXCHANGERATE_API_KEY)
            val base = rates.base

            //FIXME i'm using CURRENT_TIME for timestamp because API return data refreshed hourly, this means
            //FIXME that sometimes the data recovered from the server is already old (30 minutes refresh rule from excercise)
            //FIXME if the purpose of the excercise is to use the API timestamp, this can be achieved swapping the comment on the 2 lines below
            //val timestamp = rates.timestamp
            val timestamp = nowInSeconds

            //mapping DTO received to a list of Currency entity class
            ratesList = rates.rates.map { entry ->
                ExchangeRate(
                    entry.key, entry.value, timestamp, base
                )
            }

            Log.d(tag, "saving new rates to local DB, timestamp is ${ratesList[0].timestamp} ")
            localRepository.saveExchangeRates(ratesList)
        }

        return ratesList
    }


    /**
     * now time in seconds
     */
    private val nowInSeconds
        get() : Long {
            val nowInMillis = System.currentTimeMillis()
            return TimeUnit.MILLISECONDS.toSeconds(nowInMillis)
        }


    /**
     * this is the valid time for refreshing currency rates
     */
    private val validTime
        get() : Long {
            return nowInSeconds - TimeUnit.MINUTES.toSeconds(30)
        }


}