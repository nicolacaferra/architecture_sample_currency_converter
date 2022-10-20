package com.paypay.currency.data.exceptions

/**
 * this is a generic exception used to handle unexpected errors
 */
class GenericException : Exception(message){

    companion object {
        const val message = "An unexpected error has occurred, please contact customer support"
    }
}