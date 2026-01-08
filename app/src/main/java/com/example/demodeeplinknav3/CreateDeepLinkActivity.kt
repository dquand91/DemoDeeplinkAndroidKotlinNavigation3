package com.example.demodeeplinknav3

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.example.demodeeplinknav3.navigation.SearchKey
import com.example.demodeeplinknav3.navigation.UsersKey
import com.example.demodeeplinknav3.uitls.common.EMPTY
import com.example.demodeeplinknav3.uitls.common.EntryScreen
import com.example.demodeeplinknav3.uitls.common.FIRST_NAME_JOHN
import com.example.demodeeplinknav3.uitls.common.FIRST_NAME_JULIE
import com.example.demodeeplinknav3.uitls.common.FIRST_NAME_MARY
import com.example.demodeeplinknav3.uitls.common.FIRST_NAME_TOM
import com.example.demodeeplinknav3.uitls.common.LOCATION_BC
import com.example.demodeeplinknav3.uitls.common.LOCATION_BR
import com.example.demodeeplinknav3.uitls.common.LOCATION_CA
import com.example.demodeeplinknav3.uitls.common.LOCATION_US
import com.example.demodeeplinknav3.uitls.common.MenuDropDown
import com.example.demodeeplinknav3.uitls.common.MenuTextInput
import com.example.demodeeplinknav3.uitls.common.PaddedButton
import com.example.demodeeplinknav3.uitls.common.TextClickable
import com.example.demodeeplinknav3.uitls.common.TextContent
import com.example.demodeeplinknav3.uitls.url.*
import kotlin.collections.set

class CreateDeepLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            /**
             * UI để user tạo deeplink.
             * Nó gồm các thành phần:
             * 1. MenuDropDown để chọn path (home, include, search)
             * 2. Nếu chọn include thì hiện thêm MenuDropDown để chọn filter (recently_added, all)
             * 3. Nếu chọn search thì hiện thêm MenuTextInput và MenuDropDown để nhập các query (firstName, location, ageMin, ageMax)
             * 4. Hiển thị final url và cho phép copy vào clipboard khi click
             * 5. Nút "Deeplink Away!" để trigger deeplink đến ReceiveAndHandleDeeplinkActivity với url đã tạo
             */
            EntryScreen("Hello - Build Your Deeplink") {
                TextContent("Base url:\n${PATH_BASE}/")
                var showFilterOptions by remember { mutableStateOf(false) }
                val selectedPath = remember { mutableStateOf(MENU_OPTIONS_PATH[KEY_PATH]?.first()) }

                var showQueryOptions by remember { mutableStateOf(false) }
                var selectedFilter by remember { mutableStateOf("") }
                val selectedSearchQuery = remember { mutableStateMapOf<String, String>() }

                // manage path options
                MenuDropDown(
                    menuOptions = MENU_OPTIONS_PATH,
                ) { _, selection ->
                    selectedPath.value = selection
                    when (selection) {
                        PATH_SEARCH -> {
                            showQueryOptions = true
                            showFilterOptions = false
                        }

                        PATH_INCLUDE -> {
                            showQueryOptions = false
                            showFilterOptions = true
                        }

                        else -> {
                            showQueryOptions = false
                            showFilterOptions = false
                        }
                    }
                }

                // dùng LaunchedEffect để reset selectedFilter nếu showFilterOptions thay đổi.
                // Cụ thể:
                // - Nếu showFilterOptions = true (menu filter được mở), thì gán selectedFilter bằng giá trị đầu tiên trong MENU_OPTIONS_FILTER
                // - Nếu showFilterOptions = false (menu filter bị đóng), thì gán selectedFilter = ""
                LaunchedEffect(showFilterOptions) {
                    selectedFilter = if (showFilterOptions) {
                        MENU_OPTIONS_FILTER.values.first().first()
                    } else {
                        ""
                    }
                }
                if (showFilterOptions) {
                    MenuDropDown(
                        menuOptions = MENU_OPTIONS_FILTER,
                    ) { _, selected ->
                        selectedFilter = selected
                    }
                }

                // dùng LaunchedEffect để reset selectedSearchQuery nếu showQueryOptions thay đổi.
                // Cụ thể:
                // - Nếu showQueryOptions = true (menu query được mở), thì khởi tạo selectedSearchQuery với key là key của entry đầu tiên trong MENU_OPTIONS_SEARCH
                //   và value là giá trị đầu tiên trong list của entry đó.
                // - Nếu showQueryOptions = false (menu query bị đóng), thì clear selectedSearchQuery
                LaunchedEffect(showQueryOptions) {
                    if (showQueryOptions) {
                        val initEntry = MENU_OPTIONS_SEARCH.entries.first()
                        selectedSearchQuery[initEntry.key] = initEntry.value.first()
                    } else {
                        selectedSearchQuery.clear()
                    }
                }
                if (showQueryOptions) {
                    MenuTextInput(
                        menuLabels = MENU_LABELS_SEARCH,
                    ) { label, selected ->
                        selectedSearchQuery[label] = selected
                    }
                    MenuDropDown(
                        menuOptions = MENU_OPTIONS_SEARCH,
                    ) { label, selected ->
                        selectedSearchQuery[label] = selected
                    }
                }

                // Build arguments của deeplink dựa trên selectedPath
                val arguments = when (selectedPath.value) {
                    PATH_INCLUDE -> "/${selectedFilter}"
                    PATH_SEARCH -> {
                        buildString {
                            selectedSearchQuery.forEach { entry ->
                                if (entry.value.isNotEmpty()) {
                                    val prefix = if (isEmpty()) "?" else "&"
                                    append("$prefix${entry.key}=${entry.value}")
                                }
                            }
                        }
                    }

                    else -> ""
                }

                // Build final deeplink URL
                val finalUrl = "${PATH_BASE}/${selectedPath.value}$arguments"

                // Hiển thị final url và cho phép copy vào clipboard khi click
                TextClickable ("Final url:\n$finalUrl") {
                    this.copyToClipboard("A1", finalUrl)
                }

                // Nút "Deeplink Away!" để trigger deeplink.
                PaddedButton("Deeplink Away!") {
                    val intent = Intent(
                        this@CreateDeepLinkActivity,
                        MainActivity::class.java
                    )
                    // start activity with the url
                    intent.data = finalUrl.toUri()
                    startActivity(intent)
                }
            }
        }
    }
}

fun Context.copyToClipboard(label: String = "text", text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

private const val KEY_PATH = "path"
private val MENU_OPTIONS_PATH = mapOf(
    KEY_PATH to listOf(
        STRING_LITERAL_HOME,
        PATH_INCLUDE,
        PATH_SEARCH,
    ),
)

private val MENU_OPTIONS_FILTER = mapOf(
    UsersKey.FILTER_KEY to listOf(UsersKey.FILTER_OPTION_RECENTLY_ADDED, UsersKey.FILTER_OPTION_ALL),
)

private val MENU_OPTIONS_SEARCH = mapOf(
    SearchKey::firstName.name to listOf(
        EMPTY,
        FIRST_NAME_JOHN,
        FIRST_NAME_TOM,
        FIRST_NAME_MARY,
        FIRST_NAME_JULIE
    ),
    SearchKey::location.name to listOf(EMPTY, LOCATION_CA, LOCATION_BC, LOCATION_BR, LOCATION_US)
)

private val MENU_LABELS_SEARCH = listOf(SearchKey::ageMin.name, SearchKey::ageMax.name)