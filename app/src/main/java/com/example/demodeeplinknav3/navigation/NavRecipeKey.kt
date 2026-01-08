package com.example.demodeeplinknav3.navigation

import androidx.navigation3.runtime.NavKey
import com.example.demodeeplinknav3.uitls.url.*
import kotlinx.serialization.Serializable


// File này định nghĩa các NavKey (1 Node trong navigation graph của nav3)
// Mỗi NavKey đại diện cho một Screen.
// Bao gồm 3 NavKey tương ứng với 3 screen:
// - HomeKey - Route tới màn hình Home.
// - UsersKey - Route tới màn hình Users với tham filter.
// - SearchKey - Route tới màn hình Search với các tham số tìm kiếm khác nhau.

// Đây là Base NavKey cho tất cả các NavKey trong app.
// Property mặc định "name" để xác định tên của NavKey.
internal interface NavRecipeKey: NavKey {
    val name: String
}

// Màn hình Home với NavKey không có tham số.
// Thuộc tính "name" mặc định là giá trị của biến STRING_LITERAL_HOME
@Serializable
internal object HomeKey: NavRecipeKey {
    override val name: String = STRING_LITERAL_HOME
}

// Màn hình Users với NavKey có tham số "filter".
// Thuộc tính "name" mặc định là giá trị của biến STRING_LITERAL_USERS
// Dùng Companion object để định các biến static sử dụng trong class UsersKey.
//   Ở đây mang ý nghĩa là định nghĩa các giá trị filter có thể dùng.
//   Có 3 giá trị filter:
//   - FILTER_OPTION_RECENTLY_ADDED: "recentlyAdded" - Lọc những user mới thêm gần đây.
//   - FILTER_OPTION_ALL: "all" - Lọc tất cả user.
//   - FILTER_KEY: "filter" - Tên của tham số filter trong URL.
@Serializable
internal data class UsersKey(
    val filter: String,
): NavRecipeKey {
    override val name: String = STRING_LITERAL_USERS
    companion object {
        const val FILTER_KEY = STRING_LITERAL_FILTER
        const val FILTER_OPTION_RECENTLY_ADDED = "recentlyAdded"
        const val FILTER_OPTION_ALL = "all"
    }
}

// Màn hình Search với NavKey có các tham số tìm kiếm tùy chọn:
// - firstName: Tên đầu tiên của user.
// - ageMin: Tuổi tối thiểu của user.
// - ageMax: Tuổi tối đa của user.
// - location: Vị trí của user.
// Thuộc tính "name" mặc định là giá trị của biến STRING_LITERAL_SEARCH
@Serializable
internal data class SearchKey(
    val firstName: String? = null,
    val ageMin: Int? = null,
    val ageMax: Int? = null,
    val location: String? = null,
): NavRecipeKey {
    override val name: String = STRING_LITERAL_SEARCH
}