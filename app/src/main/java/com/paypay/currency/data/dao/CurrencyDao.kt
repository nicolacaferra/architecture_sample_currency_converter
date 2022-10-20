package com.paypay.currency.data.dao

import androidx.room.*
import com.paypay.currency.data.entities.Currency

/**
 * data access object for currency entity
 */
@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency order by symbol asc")
    fun getAll(): List<Currency>

    @Query("SELECT * FROM currency WHERE symbol = :value LIMIT 1")
    fun findBySymbol(value: String): Currency

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(currencies: Collection<Currency>)

    @Delete
    fun delete(currency: Currency)
}