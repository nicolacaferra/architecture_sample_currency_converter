package com.paypay.currency.data.dao

import androidx.room.*
import com.paypay.currency.data.entities.ExchangeRate

/**
 * data access object for exchange rate currency
 */
@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rate order by symbol asc")
    fun getAll(): List<ExchangeRate>

    @Query("SELECT * FROM exchange_rate where timestamp > :minTime order by symbol asc")
    fun getAllGreaterThanTimestamp(minTime: Long): List<ExchangeRate>

    @Query("SELECT * FROM exchange_rate WHERE symbol = :value LIMIT 1")
    fun findBySymbol(value: String): ExchangeRate

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exchangeRate: ExchangeRate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(currencies: Collection<ExchangeRate>)

    @Delete
    fun delete(exchangeRate: ExchangeRate)

}