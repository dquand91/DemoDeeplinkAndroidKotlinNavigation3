# Deeplink with Navigation version 3

Đây là ví dụ về cách implement Deeplink sử dụng Navigation version 3.

## Cách hoạt động

Ứng dụng bào gồm 3 Activity - `CreateDeepLinkActivity`, `ReceiveAndHandleDeeplinkActivity` và `MainActivity`.
- `CreateDeepLinkActivity`: Tạo và gửi (create and send) deeplink request.
- `ReceiveAndHandleDeeplinkActivity`: Nhận và xử lý (receive and handle) deeplink request.
- `MainActivity`: Màn hình chính khi mở app lên. Có chứa 1 nút để mở `CreateDeepLinkActivity`.

## Các dạng deeplink được hỗ trợ
`CreateDeepLinkActivity` có chứa Combo Box để user lựa chọn các dạng deeplink để send request deeplink:
- Có 3 dạng deeplink được hỗ trợ:
1. `HomeKey` - Deeplink với URL chính xác (không có tham số deeplink). ví dụ: https://www.example.com/home
2. `UsersKey` - Deeplink với tham số đường dẫn (path arguments). Ví dụ: https://www.example.com/users/include/recentlyAdded
3. `SearchKey` - Deeplink với tham số truy vấn (query arguments). Ví dụ: https://www.example.com/users/search?ageMax=5&ageMin=1&location=CA&firstName=Mary

## Cấu trúc App:
- Gồm 4 package chính:
1. utils:
   - Chứa các class và phương thức hỗ trợ để phân tích và so khớp deeplink.
   - Chứa các utils UI dùng chung cho cả 2 Activity.
   - Chứa các hằng số dùng cho deeplink URL.
2. ui: Chứa các file UI Jetpack Compose.
3. send: Chứa `CreateDeepLinkActivity` để tạo và gửi deeplink request.
4. receive: Chứa `ReceiveAndHandleDeeplinkActivity` để nhận và xử lý deeplink request.

## Cấu trúc của các class utils dùng để phân tích và so khớp deeplink:
- `DeeplinkRequest`: Chứa các thông tin về deeplink request như URL, tham số đường dẫn, tham số truy vấn.
- `DeepLinkPattern`: Chứa các mẫu deeplink đã định nghĩa và phương thức để so khớp Requested (input) URL deeplink với các mẫu deeplink.
- `DeeplinkMatcher`: So khớp DeeplinkRequest và DeepLinkPattern, trích xuất tham số (nếu có) và trả về kết quả so khớp.
- `DeeplinkResult`: Chứa kết quả so khớp deeplink, bao gồm thông tin về mẫu deeplink đã khớp và các tham số trích xuất (nếu có).

## Cách thức hoạt động:
1. Từ `MainActivity`, user nhấn nút để mở `CreateDeepLinkActivity`.
2. Tại `CreateDeepLinkActivity`, user chọn dạng deeplink từ Combo Box và nhấn nút "Send Deeplink".
3. App tạo URL deeplink, và gửi request deeplink sử dụng Intent.
4. `ReceiveAndHandleDeeplinkActivity` nhận Intent chứa deeplink URL.
5. `ReceiveAndHandleDeeplinkActivity` sử dụng `DeepLinkPattern` trong package `utils` để phân tích và so khớp URL deeplink với các mẫu deeplink đã định nghĩa.
6. Nếu URL deeplink khớp với mẫu deeplink, app trích xuất các tham số (nếu có) và hiển thị thông tin tương ứng trên UI.
7. Nếu URL deeplink không khớp với bất kỳ mẫu deeplink nào, app hiển thị thông báo lỗi.

## Các thư viện sử dụng, dependencies:
- navigation-compose: Phiên bản 3.0.0 trở lên.
- compose-ui: Phiên bản 1.4.0 trở lên.
- compose-material: Phiên bản 1.4.0 trở lên.
- activity-compose: Phiên bản 1.7.0 trở lên.
- kotlin-stdlib: Phiên bản 1.8.0 trở lên.

## Cách trigger deeplink để test:
- Có 2 cách để trigger deeplink:
- **Cách 1**: Sử dụng `CreateDeepLinkActivity` trong app để tạo và gửi deeplink request.
- **Cách 2:** Sử dụng adb command để gửi Intent chứa deeplink URL. Ví dụ:
    - Để deeplink đến màn hình Home:
        - ``` adb shell am start -W -a android.intent.action.VIEW -d "https://www.demodeeplinknav3.com/home" com.example.demodeeplinknav3```
        - Với: 
        - `com.example.demodeeplinknav3` là package name của app.
        - `https://www.demodeeplinknav3.com/home` là URL deeplink cần test.
    - Để deeplink đến màn hình Users với tham số đường dẫn:
        - ``` adb shell am start -W -a android.intent.action.VIEW -d "https://www.demodeeplinknav3.com/users/include/recentlyAdded" com.example.demodeeplinknav3```
    - Để deeplink đến màn hình Search với tham số truy vấn:
        - ``` adb shell am start -W -a android.intent.action.VIEW -d "https://www.demodeeplinknav3.com/users/search?ageMax=4&ageMin=1&location=BR&firstName=John" com.example.demodeeplinknav3```
