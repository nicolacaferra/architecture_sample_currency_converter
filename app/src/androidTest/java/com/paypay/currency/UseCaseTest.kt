package com.paypay.currency

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paypay.currency.data.database.AppDatabase
import com.paypay.currency.data.entities.Currency
import com.paypay.currency.data.exceptions.GenericException
import com.paypay.currency.data.repositories.HttpCurrencyRepository
import com.paypay.currency.data.repositories.LocalCurrencyRepository
import com.paypay.currency.usecases.CurrencyUseCases
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UseCaseTest {

    private lateinit var useCase: CurrencyUseCases

    @Before
    fun initUseCase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        val currencyDao = db.currencyDao()
        val exchangeRateDao = db.exchangeRateDao()

        val localRepository = LocalCurrencyRepository(exchangeRateDao, currencyDao)
        val httpRepository = HttpCurrencyRepository()

        useCase = CurrencyUseCases(httpRepository, localRepository)
    }


    @Test
    fun fetchingAllCurrencies() {
        val allCurrencies = useCase.getAllCurrencies()
        assertNotNull(allCurrencies)
    }

    @Test(expected = GenericException::class)
    fun getRateError() {
        val invalidCurrency = Currency("NOT_VALID", "invalid currency")
        useCase.getRatesByCurrencyAndAmount(invalidCurrency, 4.0)
    }

    @Test
    fun getRatesByAmountAndCurrency() {

        val amount = 5.0
        val higherAmount = 6.0
        val highestAmount = 15.0

        val allCurrencies = useCase.getAllCurrencies()
        val jpyCurrency = allCurrencies.find { currency -> currency.symbol == "JPY" }
        assertNotNull(jpyCurrency)

        //get values for amount
        val amountValues = useCase.getRatesByCurrencyAndAmount(jpyCurrency!!, amount)
        assertNotNull(amountValues)

        //JPY currency value must be null because it's the selected currency,
        // and we are removing the selected currencies from the
        // list of the currencies to show to the user
        val amountJpy = amountValues.find { currencyValue -> currencyValue.symbol == "JPY" }
        assertNull(amountJpy)

        //checking EUR value
        val amountEur = amountValues.find { value -> value.symbol == "EUR" }
        assertNotNull(amountEur)
        assertTrue(amountEur!!.amount >= 0.0)

        //get values for higher amount and comparing against amount (values fetched for 5, against values fetched for 6)
        val higherAmountValues = useCase.getRatesByCurrencyAndAmount(jpyCurrency, higherAmount)
        assertNotNull(higherAmountValues)
        val higherAmountEur = amountValues.find { currencyValue -> currencyValue.symbol == "EUR" }
        assertNotNull(higherAmountEur)
        assertTrue(higherAmountEur!!.amount >= amountEur.amount)

        //get values for highest amount and comparing against higher amount (values fetched for 6, against values fetched for 15)
        val highestAmountValues = useCase.getRatesByCurrencyAndAmount(jpyCurrency, highestAmount)
        assertNotNull(highestAmountValues)
        val highestAmountEur = amountValues.find { currencyValue -> currencyValue.symbol == "EUR" }
        assertNotNull(highestAmountEur)
        assertTrue(highestAmountEur!!.amount >= higherAmountEur.amount)
        assertTrue(highestAmountEur.amount >= amountEur.amount)
    }
}