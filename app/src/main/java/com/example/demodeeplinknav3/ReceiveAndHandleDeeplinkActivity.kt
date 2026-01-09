package com.example.demodeeplinknav3

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.demodeeplinknav3.navigation.HomeKey
import com.example.demodeeplinknav3.navigation.SearchKey
import com.example.demodeeplinknav3.navigation.UsersKey
import com.example.demodeeplinknav3.uitls.common.EntryScreen
import com.example.demodeeplinknav3.uitls.common.FriendsList
import com.example.demodeeplinknav3.uitls.common.LIST_MY_USERS
import com.example.demodeeplinknav3.uitls.common.TextContent
import com.example.demodeeplinknav3.uitls.deeplinkutils.DeepLinkMatcher
import com.example.demodeeplinknav3.uitls.deeplinkutils.DeepLinkPattern
import com.example.demodeeplinknav3.uitls.deeplinkutils.DeepLinkRequest
import com.example.demodeeplinknav3.uitls.deeplinkutils.DeeplinkKeyDecoder
import com.example.demodeeplinknav3.uitls.url.URL_HOME_EXACT
import com.example.demodeeplinknav3.uitls.url.URL_SEARCH
import com.example.demodeeplinknav3.uitls.url.URL_USERS_WITH_FILTER

/**
 * ReceiveAndHandleDeeplinkActivity sẽ nhận và xử lý deeplink dựa vào các khai báo intent filter trong AndroidManifest.xml
 *
 * Nó parse deeplink thành NavKey tương ứng và khởi tạo NavBackStack theo thứ tự:
 * Step 1: Parse các supported deeplinks (URLs mà app hỗ trợ) thành format mà app đọc được dựa vào [DeepLinkPattern]
 * Step 2: Parse input deeplinks (request deeplink) thành format mà app đọc được dựa vào [DeepLinkRequest]
 * **Note**: format của requested deeplink và supported deeplinks phải đồng nhất để dễ so sánh và tìm kiếm.
 *
 * Step 3: So sánh Requested deeplink với Supported deeplinks để tìm kiếm sự trùng khớp để tạo ra [DeepLinkMatchResult]
 * Format của [DeepLinkMatchResult] phải convert được thành backstack key [NavKey].
 *
 * Step 4: Sử dụng [DeeplinkKeyDecoder] để convert [DeeplinkMatchResult] thành backstack key [NavKey].
 *
 */
class ReceiveAndHandleDeeplinkActivity : ComponentActivity() {
    /** STEP 1. Parse supported deeplinks */
    // internal so that landing activity can link to this in the kdocs
    internal val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
        // "https://www.demodeeplinknav3.com/home"
        DeepLinkPattern(HomeKey.serializer(), (URL_HOME_EXACT).toUri()),
        // "https://www.demodeeplinknav3.com/users/include/{filter}"
        DeepLinkPattern(UsersKey.serializer(), (URL_USERS_WITH_FILTER).toUri()),
        // "https://www.demodeeplinknav3.com/users/search?{firstName}&{age}&{location}"
        DeepLinkPattern(SearchKey.serializer(), (URL_SEARCH.toUri())),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("QUAN123", "ReceiveAndHandleDeeplinkActivity onCreate: intent=${intent.data}")

        // retrieve the target Uri
        val uri: Uri? = intent.data
        // associate the target with the correct backstack key
        val key: NavKey = uri?.let {
            /** STEP 2. Parse requested deeplink */
            val request = DeepLinkRequest(uri)
            /** STEP 3. Compared Requested deeplink with Supported deeplink to find match*/
            val match = deepLinkPatterns.firstNotNullOfOrNull { pattern ->
                DeepLinkMatcher(request, pattern).match()
            }
            /** STEP 4. If match is found, associate match to the correct key*/
            match?.let {
                //leverage kotlinx.serialization's Decoder to decode
                // match result into a backstack key
                DeeplinkKeyDecoder(match.args)
                    .decodeSerializableValue(match.serializer)
            }
        } ?: HomeKey // fallback if intent.uri is null or match is not found

        /**
         * Sau khi đã lấy được NavKey từ deeplink, ta khởi tạo NavBackStack với NavKey lấy được.
         * Rồi truyền NavBackStack vào NavDisplay để hiển thị Màn hình tương ứng với NavKey.
         */
        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(key)
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<HomeKey> { navKeyHome ->
                        EntryScreen(navKeyHome.name) {
                            TextContent("<matches exact url>")
                        }
                    }
                    entry<UsersKey> { navKeyUser ->
                        EntryScreen("${navKeyUser.name} : ${navKeyUser.filter}") {
                            TextContent("<matches path argument>")
                            val list = when {
                                navKeyUser.filter.isEmpty() -> LIST_MY_USERS
                                navKeyUser.filter == UsersKey.FILTER_OPTION_ALL -> LIST_MY_USERS
                                else -> LIST_MY_USERS.take(5)
                            }
                            FriendsList(list)
                        }
                    }
                    entry<SearchKey> { navKeySearch ->
                        EntryScreen(navKeySearch.name) {
                            TextContent("<matches query parameters, if any>")
                            val matchingUsers = LIST_MY_USERS.filter { user ->
                                (navKeySearch.firstName == null || user.firstName == navKeySearch.firstName) &&
                                        (navKeySearch.location == null || user.location == navKeySearch.location) &&
                                        (navKeySearch.ageMin == null || user.age >= navKeySearch.ageMin) &&
                                        (navKeySearch.ageMax == null || user.age <= navKeySearch.ageMax)
                            }
                            FriendsList(matchingUsers)
                        }
                    }
                }
            )
        }
    }
}