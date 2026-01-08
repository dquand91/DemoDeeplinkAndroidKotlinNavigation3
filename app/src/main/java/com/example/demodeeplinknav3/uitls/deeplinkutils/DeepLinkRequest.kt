package com.example.demodeeplinknav3.uitls.deeplinkutils

import android.net.Uri

/**
 * Parse the requested Uri and store it in a easily readable format
 * Class này dùng để phân tích cú pháp của một URI deeplink và lưu trữ nó ở định dạng dễ đọc.
 *
 * @param uri: là input URI deeplink. Nó đại diện cho địa chỉ deeplink mà ứng dụng sẽ xử lý.
 */
internal class DeepLinkRequest(
    val uri: Uri
) {
    /**
     * A list of path segments
     * Ví dụ: với URI "https://www.example.com/path/to/resource",
     *      path segments sẽ là ["path", "to", "resource"]
     */
    val pathSegments: List<String> = uri.pathSegments

    /**
     * A map of query name to query value
     * Ví dụ: với URI "https://www.example.com/resource?key1=value1&key2=value2",
     *      queries sẽ là {"key1": "value1", "key2": "value2"}
     */
    val queries = buildMap {
        uri.queryParameterNames.forEach { argName ->
            this[argName] = uri.getQueryParameter(argName)!!
        }
    }

    // TODO add parsing for other Uri components, i.e. fragments, mimeType, action
    // TODO thêm phân tích cú pháp cho các thành phần khác của Uri, ví dụ: fragments, mimeType, action
}