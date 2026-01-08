package com.example.demodeeplinknav3.uitls.deeplinkutils

import android.util.Log
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlin.collections.forEach
import kotlin.invoke
import kotlin.text.get

/**
 * Matcher dùng để convert input DeeplinkRequest thành một DeepLinkPattern tương ứng.
 * Từ DeeplinkPattern này, ta có thể lấy được data dạng serializer (NavKey) và các argument để app có thể sử dụng.
 * Nó sẽ đi so sánh từng phần của DeeplinkRequest với DeepLinkPattern,
 * nếu tất cả các phần đều khớp, nó sẽ trả về một DeepLinkMatchResult chứa serializer của NavKey và map của các argument đã được parse.
 */
internal class DeepLinkMatcher<T : NavKey>(
    val request: DeepLinkRequest,
    val deepLinkPattern: DeepLinkPattern<T>
) {
    /**
     * Match a [DeepLinkRequest] to a [DeepLinkPattern].
     *
     * Returns a [DeepLinkMatchResult] if this matches the pattern, returns null otherwise
     */
    fun match(): DeepLinkMatchResult<T>? {
        if (request.pathSegments.size != deepLinkPattern.pathSegments.size) return null
        // exact match (url does not contain any arguments)
        // Nếu Uri của input deeplink trùng khớp hoàn toàn với uriPattern của deepLinkPattern,
        // thì trả về một DeepLinkMatchResult với serializer và một map rỗng (không có argument).
        // Ví dụ: uriPattern là "abc.com/home", request.uri cũng là "abc.com/home" -> khớp hoàn toàn.
        //     Thì trả về DeepLinkMatchResult cụ thể là "abc.com/home" và argument map rỗng.
        if (request.uri == deepLinkPattern.uriPattern)
            return DeepLinkMatchResult(deepLinkPattern.serializer, mapOf())

        // Nếu không khớp hoàn toàn, ta sẽ đi so sánh từng phần của input deeplink với deepLinkPattern.
        val args = mutableMapOf<String, Any>()


        // Parse Path Segments, bằng cách so sánh
        // Gồm các bước:
        // 1. Lấy danh sách path segments từ request.
        // 2. Dùng zip để ghép từng path segment của request với path segment tương ứng trong deepLinkPattern.
        // 3. Duyệt qua từng cặp path segment đã ghép.
        // 4. Với mỗi cặp, kiểm tra xem path segment trong deepLinkPattern có phải là một argument (isParamArg) hay không.
        //    - Nếu là argument, cố gắng parse path segment từ request thành kiểu dữ liệu mong đợi (typeParser).
        //      Nếu parse thành công, lưu tên argument và giá trị đã parse vào map args.
        //      Nếu parse thất bại, ghi log lỗi và trả về null (không khớp).
        //    - Nếu không phải là argument, so sánh trực tiếp giá trị của hai path segment.
        //      Nếu không khớp, trả về null (không khớp).
        // Ví dụ: pathSegments của request là ["users", "123", "profile"]
        //      pathSegments của deepLinkPattern là ["users", "{userId}", "profile"]
        //      - Với cặp ("users", "users"): khớp trực tiếp.
        //      - Với cặp ("123", "{userId}"): là argument, parse "123" thành kiểu dữ liệu mong đợi (ví dụ Int), lưu vào args với key "userId".
        //      - Với cặp ("profile", "profile"): khớp trực tiếp.

        // Nếu tất cả các path segment đều khớp, tiếp tục kiểm tra các query parameters.
        request.pathSegments
            .asSequence()
            // zip để so sánh hai đối tượng cạnh nhau, thứ tự ở đây rất quan trọng nên chúng ta
            // cần đảm bảo các segment được so sánh ở cùng vị trí trong url
            // Ở đây sẽ bắt cặp từng path segment giữa input deeplink và deepLinkPattern.
            // Ví dụ:
            // - input deeplink path segments: ["users", "123", "profile"]
            // - deepLinkPattern path segments: ["users", "{userId}", "profile"]
            // Kết quả sau khi zip sẽ là:
            // [("users", "users"), ("123", "{userId}"), ("profile", "profile")]
            .zip(deepLinkPattern.pathSegments.asSequence())
            .forEach { it ->
                // Lấy ra hai path segment để so sánh.
                val requestedSegment = it.first
                val candidateSegment = it.second

                // Nếu segment trong deepLinkPattern là một argument,
                // cố gắng parse segment từ input deeplink thành kiểu dữ liệu mong đợi.
                if (candidateSegment.isParamArg) {
                    val parsedValue = try {
                        candidateSegment.typeParser.invoke(requestedSegment)
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG_LOG_ERROR, "Failed to parse path value:[$requestedSegment].", e)
                        return null
                    }
                    args[candidateSegment.stringValue] = parsedValue
                } else if(requestedSegment != candidateSegment.stringValue){
                    // if it's path arg is not the expected type, its not a match
                    return null
                }
            }

        // Sau khi parse cái path segments xong,
        // Tiếp tục parse các query parameters (nếu có).
        request.queries.forEach { query ->
            val name = query.key
            val queryStringParser = deepLinkPattern.queryValueParsers[name]
            val queryParsedValue = try {
                queryStringParser!!.invoke(query.value)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG_LOG_ERROR, "Failed to parse query name:[$name] value:[${query.value}].", e)
                return null
            }
            args[name] = queryParsedValue
        }

        // Trả về DeepLinkMatchResult với serializer và map các argument đã parse.
        return DeepLinkMatchResult(deepLinkPattern.serializer, args)
    }
}


/**
 * Object chứa kết quả trả về sau khi đã parse được 1 deeplink thành công.
 *
 * @param [T] kiểu NavKey, là màn hình hoặc điểm đến tương ứng với deeplink đã được parse thành công.
 * @param serializer serializer for [T]
 * @param args The map of argument name to argument value. The value is expected to have already
 * been parsed from the raw url string back into its proper KType as declared in [T].
 * @param args là map chứa tên argument và giá trị tương ứng. Giá trị trong map này đã được parse từ chuỗi URL deeplink thô thành kiểu dữ liệu đúng như đã khai báo trong [T].
 * Ví dụ 1 DeeplinkMatchResult<SearchKey> sẽ có:
 * - serializer: serializer của SearchKey
 * - args: {"firstName": "John", "ageMin": 18, "ageMax": 30, "location": "New York"}
 * */
internal data class DeepLinkMatchResult<T : NavKey>(
    val serializer: KSerializer<T>,
    val args: Map<String, Any>
)

const val TAG_LOG_ERROR = "QUAN123_DEEPLINK_ERROR"