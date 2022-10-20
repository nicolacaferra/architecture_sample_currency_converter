package com.paypay.currency

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * extending android application class in order to use Hilt dependency injection
 */
@HiltAndroidApp
class CurrencyApplication : Application()