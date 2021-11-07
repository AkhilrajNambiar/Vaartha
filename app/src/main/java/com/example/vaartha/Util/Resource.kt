package com.example.vaartha.Util

// Used to wrap around the Network requests: Recommended by google
// Used to check between Success, Failure or Loading states
// This is a generic and sealed class class
// sealed classes allow only some particular classes to inherit them
// So here only classes that are allowed by us, will be able to inherit
// from the Resource class
sealed class Resource<T>(
    val data: T? = null, //The response body
    val message: String? = null
) {
    //Now we define the classes that are allowed to inherit Resource

    // In case of success we always obtain a response body
    class Success<T>(data: T): Resource<T>(data)
    // In case of errors we always have an error message and optionally
    // a response body
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
    class Loading<T>: Resource<T>()
}




