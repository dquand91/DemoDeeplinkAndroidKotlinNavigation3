package com.example.demodeeplinknav3.uitls.deeplinkutils

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import java.io.Serializable

/**
 * Đây là Parser để convert supported deeplink (deeplink mà app chấp nhận) thành dạng đối tượng app có thể đọc được.
 *
 * Các ghi chú sau áp dụng cụ thể cho project này thôi, tùy từng project sẽ có cách implement khác nhau:
 *
 * Supported Deeplink được xây dựng từ một backstack key [T] có thể serialize được và hỗ trợ deeplink.
 * Điều này có nghĩa là nếu deeplink này chứa bất kỳ tham số nào (path hoặc query),
 * tên tham số phải khớp với bất kỳ tên trường thành viên nào của [T].
 *
 * [DeepLinkPattern] nên được tạo tương ứng cho từng supported deeplink được.
 * Điều này có nghĩa là nếu [T] hỗ trợ 2 deeplink patterns:
 *
 * ```
 *  val deeplink1 = www.demodeeplinknav3.com/home
 *  val deeplink2 = www.demodeeplinknav3.com/profile/{userId}
 *  ```
 * Thì Cần phải tạo 2 [DeepLinkPattern] tương ứng:
 * ```
 * val parsedDeeplink1 = DeepLinkPattern(T.serializer(), deeplink1)
 * val parsedDeeplink2 = DeepLinkPattern(T.serializer(), deeplink2)
 * ```
 *
 * Đoạn implement này giả định một vài điều:
 * 1. tất cả các path arguments là bắt buộc/không thể null - khớp 1 phần sẽ được coi là không khớp.
 *      Ví dụ: deeplink pattern là /profile/{userId}, thì /profile sẽ KHÔNG khớp.
 * 2. tất cả các query arguments OPTIONAL (có thể null hoặc có giá trị mặc định)
 *      Ví dụ: deeplink pattern là /search?firstName={firstName}, thì /search vẫn KHỚP.
 *
 * @param T là kiểu backstack key (NavKey) hỗ trợ deeplinking của [uriPattern]
 * @param serializer là serializer của [T]. KSerializer được dùng để đọc metadata của [T], từ đó biết được kiểu dữ liệu của từng argument.
 * @param uriPattern là input uri pattern của supported deeplink, ví dụ: "abc.com/home/{pathArg}"
 */
internal class DeepLinkPattern<T : NavKey>(
    val serializer: KSerializer<T>,
    val uriPattern: Uri
) {
    /**
     * Regex để hỗ trợ phân biệt xem một path segment là argument hay là static value
     * Ví dụ: với path segment "{userId}", regex này sẽ tìm ra "userId" là tên argument.
     *      Còn với path segment "{idNumber}", regex này sẽ tìm ra "idNumber" là tên argument.
     *      Ngược lại, với path segment "home", regex này sẽ không tìm thấy gì cả.
     */
    private val regexPatternFillIn = Regex("\\{(.+?)\\}")


    /**
     * Phân tích cú pháp của path thành một danh sách các [PathSegment]
     * Ở đây, thứ tự của các path segments rất quan trọng -
     * các path segments cần phải khớp về giá trị và thứ tự khi so sánh giữa input deeplink và supported deeplink.
     * Ví dụ: với deeplink pattern "/users/{userId}/profile",
     *      thì path segments sẽ là:
     *      - Segment 1: "users" (static value)
     *      - Segment 2: "{userId}" (argument)
     *      - Segment 3: "profile" (static value)
     */
    val pathSegments: List<PathSegment>  = buildList {
        uriPattern.pathSegments.forEach { segment ->
            // First, kiểm tra xem segment này có phải là path argument không
            var result = regexPatternFillIn.find(segment)
            if (result != null) {
                // Nếu đúng, trích xuất tên của path argument (giá trị bên trong dấu ngoặc nhọn)
                val argName = result.groups[1]!!.value
                // Thông qua Serializer kiểu [T], đọc kiểu primitive của argument này để lấy đúng type parser
                val elementIndex = serializer.descriptor.getElementIndex(argName)
                val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
                // Cuối cùng, thêm tên argument và type parser tương ứng vào danh sách
                add(PathSegment(argName, true,getTypeParser(elementDescriptor.kind)))
            } else {
                // Nếu không phải là path argument, thì nó chỉ là một static string path segment
                add(PathSegment(segment,false, getTypeParser(PrimitiveKind.STRING)))
            }
        }
    }

    /**
     * Parse các supported queries thành một map từ queryParameterNames sang [TypeParser]
     * Nó sẽ được sử dụng sau này để phân tích cú pháp của một giá trị query được cung cấp thành đúng KType
     * Ví dụ: với query parameter "ageMin" có kiểu Int,
     *      thì map này sẽ chứa một entry với key là "ageMin" và value là hàm String::toInt
     *      để chuyển đổi giá trị String thành Int.
     */
    val queryValueParsers: Map<String, TypeParser> = buildMap {
        uriPattern.queryParameterNames.forEach { paramName ->
            val elementIndex = serializer.descriptor.getElementIndex(paramName)
            val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
            this[paramName] = getTypeParser(elementDescriptor.kind)
        }
    }

    /**
     * Metadata của một supported path segment
     * Ví dụ: với path segment "{userId}" có kiểu Int,
     *      thì PathSegment này sẽ có:
     *      - stringValue: "userId"
     *      - isParamArg: true
     *      - typeParser: hàm String::toInt để chuyển đổi giá trị String thành Int
     */
    class PathSegment(
        val stringValue: String,
        val isParamArg: Boolean,
        val typeParser: TypeParser
    )
}

/**
 * TypeParser là kiểu Alias rút gọn cho hàm convert từ String sang Serializable.
 * Ví dụ: String::toInt, String::toBoolean, Any::toString, v.v.
 * Nếu viết dài dòng thì "String::toInt" sẽ là: "(String) -> Serializable"
 * Với hàm "String::toInt":
 *      - hàm "toInt" là hàm extension của lớp String.
 *      - Input: String (giá trị String cần convert)
 *      - Output: Int (Int kế thừa từ Serializable)
 * Với "(String) -> Serializable":
 *      - Input: String
*       - Output: Serializable (Int, Boolean, String đều kế thừa từ Serializable)
 *  Nếu viết ngắn gọn bằng TypeAlias thì sẽ là: TypeParser
 */
private typealias TypeParser = (String) -> Serializable

/**
 * Lấy TypeParser tương ứng với SerialKind (kiểu dữ liệu primitive)
 * Ví dụ: với SerialKind.INT, hàm này sẽ trả về String::toInt
 *     với SerialKind.BOOLEAN, hàm này sẽ trả về String::toBoolean
 */
private fun getTypeParser(kind: SerialKind): TypeParser {
    return when (kind) {
        PrimitiveKind.STRING -> Any::toString
        PrimitiveKind.INT -> String::toInt
        PrimitiveKind.BOOLEAN -> String::toBoolean
        PrimitiveKind.BYTE -> String::toByte
        PrimitiveKind.CHAR -> String::toCharArray
        PrimitiveKind.DOUBLE -> String::toDouble
        PrimitiveKind.FLOAT -> String::toFloat
        PrimitiveKind.LONG -> String::toLong
        PrimitiveKind.SHORT -> String::toShort
        else -> throw IllegalArgumentException(
            "Unsupported argument type of SerialKind:$kind. The argument type must be a Primitive."
        )
    }
}