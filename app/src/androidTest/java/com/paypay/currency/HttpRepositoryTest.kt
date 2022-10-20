package com.paypay.currency

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paypay.currency.data.exceptions.NetworkException
import com.paypay.currency.data.repositories.HttpCurrencyRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class HttpRepositoryTest {

    private lateinit var repository: HttpCurrencyRepository

    @Before
    fun setupRepository() {
        repository = HttpCurrencyRepository()
    }


    @Test
    fun fetchingCurrencies() {
        val currenciesList = repository.fetchAllCurrencies()
        Assert.assertNotNull(currenciesList)
    }

    @Test
    fun fetchingExchangeRates() {
        val exchangeRates =
            repository.fetchLatestExchangeRates(BuildConfig.OPENEXCHANGERATE_API_KEY)
        Assert.assertNotNull(exchangeRates)
    }

    @Test(expected = NetworkException::class)
    fun fetchingExchangeRatesError() {
        val exchangeRates = repository.fetchLatestExchangeRates("INVALID_API_KEY")
        Assert.assertNotNull(exchangeRates)
    }

}