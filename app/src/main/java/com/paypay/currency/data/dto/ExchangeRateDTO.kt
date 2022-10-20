package com.paypay.currency.data.dto

/**
 * dto used to map returning json from openexchangerates API
 */
data class ExchangeRateDTO(val timestamp: Long, val base: String, val rates: Map<String, Double>)
