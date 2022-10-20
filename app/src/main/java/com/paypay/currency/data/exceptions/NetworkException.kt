package com.paypay.currency.data.exceptions

/**
 * this is a custom exception used to handle network errors
 */
class NetworkException : Exception(message){

    companion object {
        const val message = "An error occurred while retrieving data from the server"
    }
}