package com.nisrulz.example.spacexapi.common.contract.utils

// Type-alias for a callback that takes no arguments and returns nothing (Unit)
typealias VoidCallback = () -> Unit

// Type-alias for a callback that takes one argument of type Type and returns nothing (Unit)
typealias ValueCallback<Type> = (value: Type) -> Unit
