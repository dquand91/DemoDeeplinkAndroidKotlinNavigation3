package com.example.demodeeplinknav3.uitls.url

import com.example.demodeeplinknav3.navigation.SearchKey

/**
 * File này định nghĩa các hằng số URL và chuỗi ký tự được sử dụng khi tạo và xử lý deeplink
 * Chứa các query parameters, path segments, và các URL mẫu
 * Ví dụ: https://www.demodeeplinknav3.com/users/search?firstName={firstName}&ageMin={ageMin}&ageMax={ageMax}&location={location}
 * https://www.demodeeplinknav3.com/users/home
 * https://www.demodeeplinknav3.com/users/include/{filter}
 */
internal const val STRING_LITERAL_FILTER = "filter"
internal const val STRING_LITERAL_HOME = "home"
internal const val STRING_LITERAL_USERS = "users"
internal const val STRING_LITERAL_SEARCH = "search"
internal const val STRING_LITERAL_INCLUDE = "include"
internal const val PATH_BASE = "https://www.demodeeplinknav3.com"

// Path segments, example: "users/include", "users/search"
internal const val PATH_INCLUDE = "$STRING_LITERAL_USERS/$STRING_LITERAL_INCLUDE"
internal const val PATH_SEARCH = "$STRING_LITERAL_USERS/$STRING_LITERAL_SEARCH"

// Exact URL matching "https://www.demodeeplinknav3.com/home"
internal const val URL_HOME_EXACT = "$PATH_BASE/$STRING_LITERAL_HOME"

// URL with path argument "https://www.demodeeplinknav3.com/users/with/{filter}"
internal const val URL_USERS_WITH_FILTER = "$PATH_BASE/$PATH_INCLUDE/{$STRING_LITERAL_FILTER}"

// URL with query parameters "https://www.demodeeplinknav3.com/users/search?{firstName}&{age}&{location}"
internal val URL_SEARCH = "$PATH_BASE/$PATH_SEARCH" +
        "?${SearchKey::ageMin.name}={${SearchKey::ageMin.name}}" +
        "&${SearchKey::ageMax.name}={${SearchKey::ageMax.name}}" +
        "&${SearchKey::firstName.name}={${SearchKey::firstName.name}}" +
        "&${SearchKey::location.name}={${SearchKey::location.name}}"