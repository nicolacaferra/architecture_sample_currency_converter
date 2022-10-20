package com.paypay.currency

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paypay.currency.data.database.AppDatabase
import com.paypay.currency.data.entities.Currency
import com.paypay.currency.data.entities.ExchangeRate
import com.paypay.currency.data.repositories.LocalCurrencyRepository
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LocalRepositoryTest {

    private lateinit var repository: LocalCurrencyRepository

    @Before
    fun setupRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        val currencyDao = db.currencyDao()
        val exchangeRateDao = db.exchangeRateDao()

        repository = LocalCurrencyRepository(exchangeRateDao, currencyDao)
    }


    @Test
    fun savingAndRetrieveCurrencies() {

        val usd = Currency("USD", "Dollaro")
        val eur = Currency("EUR", "Euro")
        val currencies = arrayListOf(usd, eur)

        assertEquals(currencies.size, 2)

        //saving currencies
        repository.saveCurrencies(currencies)

        //retrieving saved currencies
        val persistedCurrencies = repository.getCurrencies()

        Assert.assertNotNull(persistedCurrencies)
        assertEquals(currencies.size, persistedCurrencies.size)
    }

    @Test
    fun savingAndRetrieveExchangeRates() {

        val rateUsd = ExchangeRate("USD", 1.0, System.currentTimeMillis(), "USD")
        val rateEur = ExchangeRate("EUR", 1.05, System.currentTimeMillis(), "USD")
        val rateJpy = ExchangeRate("JPY", 144.0, System.currentTimeMillis(), "USD")
        val rates = arrayListOf(rateUsd, rateEur, rateJpy)

        assertEquals(rates.size, 3)

        //saving currencies
        repository.saveExchangeRates(rates)

        //retrieving saved currencies
        var minTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        var persistedRates = repository.getRatesWithTimeGreaterThan(minTime)

        Assert.assertNotNull(persistedRates)
        assertEquals(rates.size, persistedRates.size)


        //retrieving saved currencies
        minTime = System.currentTimeMillis() + 1
        persistedRates = repository.getRatesWithTimeGreaterThan(minTime)

        Assert.assertTrue(persistedRates.isEmpty())
    }
}