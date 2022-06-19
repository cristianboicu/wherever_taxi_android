package com.cristianboicu.wherevertaxi.data

import java.io.Serializable

data class User(
    public val fname: String? = null,
    public val sname: String? = null,
    public val phone: String? = null,
    public val email: String? = null,
) : Serializable {

}