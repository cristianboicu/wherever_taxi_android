package com.cristianboicu.wherevertaxi.data

import java.io.Serializable

class User(
    public val fname: String? = null,
    public val sname: String? = null,
    public val phone: String?,
    public val email: String? = null,
) : Serializable {
}