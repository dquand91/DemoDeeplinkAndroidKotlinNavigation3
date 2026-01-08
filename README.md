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
   - Chứa các class và phương thức hỗ trợ để phân tích và khớp deeplink.
   - Chứa các utils UI dùng chung cho cả 2 Activity.
   - Chứa các hằng số dùng cho deeplink URL.
2. ui: Chứa các file UI Jetpack Compose.
3. send: Chứa `CreateDeepLinkActivity` để tạo và gửi deeplink request.
4. receive: Chứa `ReceiveAndHandleDeeplinkActivity` để nhận và xử lý deeplink request.

## Các thư viện sử dụng, dependencies:
- navigation-compose: Phiên bản 3.0.0 trở lên.
- compose-ui: Phiên bản 1.4.0 trở lên.
- compose-material: Phiên bản 1.4.0 trở lên.
- activity-compose: Phiên bản 1.7.0 trở lên.
- kotlin-stdlib: Phiên bản 1.8.0 trở lên.