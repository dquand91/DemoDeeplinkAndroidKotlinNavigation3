package com.example.demodeeplinknav3.uitls.common

import kotlinx.serialization.Serializable

@Serializable
public data class MyUser(
    val firstName: String,
    val age: Int,
    val location: String,
)

/**
 * User data
 */
public const val FIRST_NAME_JOHN = "John"
public const val FIRST_NAME_TOM = "Tom"
public const val FIRST_NAME_MARY = "Mary"
public const val FIRST_NAME_JULIE = "Julie"
public const val LOCATION_CA = "CA"
public const val LOCATION_BC = "BC"
public const val LOCATION_BR = "BR"
public const val LOCATION_US = "US"
public const val EMPTY = ""
public val LIST_MY_USERS = listOf(
    MyUser(FIRST_NAME_JOHN, 15, LOCATION_CA),
    MyUser(FIRST_NAME_JOHN, 22, LOCATION_BC),
    MyUser(FIRST_NAME_JOHN, 22, LOCATION_BR),
    MyUser(FIRST_NAME_JOHN, 22, LOCATION_US),
    MyUser(FIRST_NAME_TOM, 25, LOCATION_CA),
    MyUser(FIRST_NAME_TOM, 68, LOCATION_BR),
    MyUser(FIRST_NAME_TOM, 94, LOCATION_BC),
    MyUser(FIRST_NAME_TOM, 22, LOCATION_US),
    MyUser(FIRST_NAME_JULIE, 48, LOCATION_BR),
    MyUser(FIRST_NAME_JULIE, 33, LOCATION_US),
    MyUser(FIRST_NAME_JULIE, 46, LOCATION_CA),
    MyUser(FIRST_NAME_JULIE, 37, LOCATION_BC),
    MyUser(FIRST_NAME_MARY, 51, LOCATION_US),
    MyUser(FIRST_NAME_MARY, 63, LOCATION_BR),
    MyUser(FIRST_NAME_MARY, 5, LOCATION_CA),
    MyUser(FIRST_NAME_MARY, 52, LOCATION_BC),
)

public val LIST_FIRST_NAMES = listOf(
    FIRST_NAME_JOHN,
    FIRST_NAME_TOM,
    FIRST_NAME_MARY,
    FIRST_NAME_JULIE
)

public val LIST_LOCATIONS = listOf(
    LOCATION_CA, LOCATION_BC, LOCATION_BR, LOCATION_US
)