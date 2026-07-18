# Quy tắc đặt tên file và cấu trúc thư mục React

Tài liệu này dùng cho team khi phát triển frontend React cho dự án Cinema Management. Mục tiêu là giúp code dễ đọc, dễ tìm file, giảm conflict khi làm việc nhóm trên GitHub, và thuận tiện khi viết document đặc tả sau này.

---

## 1. Nguyên tắc chung

### 1.1. Đặt tên theo nghiệp vụ, không đặt tên mơ hồ

Tên file/thư mục phải thể hiện rõ nó thuộc chức năng nào và làm nhiệm vụ gì.

Nên đặt:

```txt
ConcessionListPage.jsx
ConcessionTable.jsx
CashierCart.jsx
movieApi.js
useCashierCart.js
```

Không nên đặt:

```txt
Page.jsx
Table.jsx
Data.jsx
test.jsx
newFile.jsx
abc.js
```

### 1.2. Một file chỉ nên làm một nhiệm vụ chính

Ví dụ:

```txt
ConcessionListPage.jsx
```

chỉ nên lo trang danh sách concession.

Không nên nhét chung:

```txt
list + create form + edit form + delete modal + API fetch
```

vào cùng một file quá lớn.

### 1.3. Tên file phải thống nhất toàn project

Nếu team chọn `ConcessionListPage.jsx` thì các feature khác cũng nên theo kiểu đó:

```txt
MovieListPage.jsx
VoucherListPage.jsx
RoomListPage.jsx
UserListPage.jsx
```

Không nên lẫn lộn:

```txt
MoviePage.jsx
VoucherScreen.jsx
RoomView.jsx
UserList.jsx
```

---

## 2. Quy tắc đặt tên folder

Folder dùng chữ thường. Nếu có nhiều từ, dùng `kebab-case`.

### Đúng

```txt
features/
shared/
layouts/
components/
pages/
hooks/
styles/
cashier-concessions/
movie-management/
booking-flow/
```

### Sai

```txt
Features/
SharedComponents/
cashier_concessions/
MovieManagement/
bookingFlow/
```

### Gợi ý thực tế

Với dự án lớn, nên chia theo feature:

```txt
src/
├── shared/
└── features/
    ├── concessions/
    ├── movies/
    ├── vouchers/
    ├── rooms/
    ├── tickets/
    ├── payments/
    └── users/
```

Nếu một feature có nhiều vai trò khác nhau, có thể tách tiếp theo role:

```txt
features/
└── concessions/
    ├── api/
    ├── admin/
    └── cashier/
```

---

## 3. Quy tắc đặt tên file React Component

File chứa React component dùng `PascalCase.jsx`.

### Đúng

```txt
ConcessionTable.jsx
ConcessionPagination.jsx
ConcessionListPage.jsx
CashierConcessionCard.jsx
CashierCart.jsx
AdminLayout.jsx
AdminSidebar.jsx
AdminNavbar.jsx
Loading.jsx
ErrorMessage.jsx
ConfirmModal.jsx
```

### Sai

```txt
concessionTable.jsx
concession-table.jsx
concession_table.jsx
cashiercart.jsx
admin-layout.jsx
```

### Quy tắc nhớ nhanh

```txt
File render UI bằng JSX → PascalCase.jsx
```

Ví dụ:

```jsx
export default function ConcessionTable() {
  return <table>...</table>;
}
```

File nên tên là:

```txt
ConcessionTable.jsx
```

---

## 4. Quy tắc đặt tên Page

Page là component đại diện cho một màn hình hoặc một route.

Tên page dùng format:

```txt
<Feature><Action>Page.jsx
```

### Ví dụ

```txt
ConcessionListPage.jsx
ConcessionCreatePage.jsx
ConcessionEditPage.jsx
ConcessionDetailPage.jsx

MovieListPage.jsx
MovieCreatePage.jsx
MovieEditPage.jsx
MovieDetailPage.jsx

CashierConcessionPage.jsx
BookingSeatPage.jsx
PaymentResultPage.jsx
```

### Không nên đặt

```txt
Concession.jsx
List.jsx
Create.jsx
Screen.jsx
Main.jsx
```

Vì khi project lớn sẽ khó biết file thuộc feature nào.

---

## 5. Quy tắc đặt tên Component

Component nhỏ nằm trong `components/` và thường chỉ phục vụ một page hoặc một feature.

Tên component dùng format:

```txt
<Feature><Purpose>.jsx
```

### Ví dụ

```txt
ConcessionTable.jsx
ConcessionPagination.jsx
ConcessionSearchBar.jsx
ConcessionForm.jsx
ConcessionDeleteModal.jsx

CashierConcessionCard.jsx
CashierCart.jsx
CashierCartItem.jsx
```

Nếu component dùng chung nhiều feature, đặt trong `shared/components/`:

```txt
Pagination.jsx
Loading.jsx
ErrorMessage.jsx
ConfirmModal.jsx
FormInput.jsx
PriceText.jsx
```

---

## 6. Quy tắc đặt tên Layout

Layout là khung giao diện dùng chung cho nhiều trang.

Dùng format:

```txt
<Role>Layout.jsx
```

### Ví dụ

```txt
AdminLayout.jsx
CustomerLayout.jsx
CashierLayout.jsx
GuestLayout.jsx
AuthLayout.jsx
```

Layout nên để trong:

```txt
src/shared/layouts/
```

hoặc:

```txt
src/layouts/
```

Ví dụ:

```txt
shared/
└── layouts/
    ├── AdminLayout.jsx
    ├── CashierLayout.jsx
    └── CustomerLayout.jsx
```

---

## 7. Quy tắc đặt tên Hook

Custom hook dùng `camelCase`, bắt đầu bằng chữ `use`.

### Đúng

```txt
useCashierCart.js
usePagination.js
useAuth.js
useDebounce.js
useFetch.js
useConfirmModal.js
```

### Sai

```txt
CashierCartHook.js
cashierCart.js
cart.js
UseCart.js
```

### Ví dụ

```js
export function useCashierCart() {
  // logic cart
}
```

---

## 8. Quy tắc đặt tên API file trong React

Folder `api/` trong React không phải backend API. Nó là nơi chứa các hàm API client để gọi backend thông qua `shared/api/httpClient.js`.

Không gọi `fetch` trực tiếp trong page/component. Page chỉ gọi function trong feature API file như `getConcessions()`, `createConcession()`, `updateConcession()`.

API file dùng `camelCase.js`.

### Đúng

```txt
concessionApi.js
movieApi.js
voucherApi.js
ticketApi.js
paymentApi.js
authApi.js
httpClient.js
```

### Sai

```txt
ConcessionAPI.js
concession-api.js
apiConcession.js
CallApi.js
```

### Ví dụ

```txt
features/
└── concessions/
    └── api/
        └── concessionApi.js
```

Trong file:

```js
import { httpClient } from "../../../shared/api/httpClient";

export function getConcessions(page, pageSize) {
  return httpClient.get(`/concessions?page=${page}&pageSize=${pageSize}`);
}

export function getConcessionById(id) {
  return httpClient.get(`/concessions/${id}`);
}

export function createConcession(payload) {
  return httpClient.post("/concessions", payload);
}

export function updateConcession(id, payload) {
  return httpClient.patch(`/concessions/${id}`, payload);
}

export function deleteConcession(id) {
  return httpClient.delete(`/concessions/${id}`);
}
```

---

## 9. Quy tắc đặt tên file CSS

CSS dùng `kebab-case.css`.

### Đúng

```txt
concession-admin.css
concession-cashier.css
movie-list.css
dashboard-layout.css
booking-seat.css
```

### Sai

```txt
ConcessionAdmin.css
concession_admin.css
concessionAdmin.css
styleConcession.css
```

### Khi nào cần CSS riêng?

Ưu tiên dùng CSS chung trong:

```txt
public/assets/css/
```

Ví dụ:

```txt
bootstrap.min.css
style.css
dashboard.css
```

Chỉ tạo CSS riêng khi page có style đặc thù.

Ví dụ admin concession cần ảnh nhỏ trong table:

```css
.concession-image-frame {
  width: 100px;
  height: 100px;
  overflow: hidden;
}
```

Cashier concession cần ảnh card lớn hơn:

```css
.cashier-concession-image {
  height: 200px;
  object-fit: cover;
}
```

Không nên sửa `style.css` global nếu chỉ một màn hình cần style đó.

---

## 10. Quy tắc đặt tên biến môi trường

Biến môi trường của Vite phải bắt đầu bằng `VITE_`.

### Đúng

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Cinema Management
```

### Sai

```env
API_BASE_URL=http://localhost:8080
BACKEND_URL=http://localhost:8080
```

Trong code React:

```js
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
```

---

## 11. Quy tắc đặt tên route frontend

Route nên rõ nghĩa, viết thường, dùng dấu `-` nếu có nhiều từ.

### Admin route

```txt
/admin/concessions
/admin/movies
/admin/vouchers
/admin/rooms
/admin/users
```

### Cashier route

```txt
/cashier/concessions
/cashier/showtimes
/cashier/tickets
```

### Customer route

```txt
/movies
/movies/:id
/booking/:showtimeId
/checkout
/profile
```

Không nên đặt route quá mơ hồ:

```txt
/list
/detail
/page1
/manage
```

---

## 12. Quy tắc đặt tên function

Function dùng `camelCase`.

### API function

```js
getConcessions()
getConcessionById()
createConcession()
updateConcession()
deleteConcession()
```

### Event handler

```js
handleAddToCart()
handlePageChange()
handleSearchChange()
handleDeleteClick()
handleSubmit()
```

### Utility function

```js
formatCurrency()
formatDate()
buildImageUrl()
```

---

## 13. Quy tắc đặt tên state trong React

State dùng tên rõ nghĩa, không viết tắt quá mức.

### Đúng

```js
const [concessions, setConcessions] = useState([]);
const [currentPage, setCurrentPage] = useState(1);
const [totalPages, setTotalPages] = useState(0);
const [loading, setLoading] = useState(false);
const [error, setError] = useState(null);
```

### Sai

```js
const [data, setData] = useState([]);
const [p, setP] = useState(1);
const [err, setErr] = useState(null);
const [x, setX] = useState(false);
```

Ngoại lệ: trong callback ngắn có thể dùng `item`, `prev`, `res`.

---

## 14. Quy tắc đặt tên DTO phía backend

Backend nên chia DTO theo `request` và `response`.

```txt
dto/
├── request/
│   └── ConcessionRequestDTO.java
└── response/
    ├── ConcessionListItemDTO.java
    ├── ConcessionDetailResponseDTO.java
    ├── PageResponseDTO.java
    └── PaginationDTO.java
```

### Request DTO

Dùng cho dữ liệu client gửi lên backend.

```txt
ConcessionRequestDTO.java
MovieRequestDTO.java
VoucherRequestDTO.java
```

### Response DTO

Dùng cho dữ liệu backend trả về frontend.

```txt
ConcessionListItemDTO.java
ConcessionDetailResponseDTO.java
MovieDetailResponseDTO.java
PageResponseDTO.java
```

### Không nên trả Entity trực tiếp

Không nên để API trả thẳng:

```java
Concession
Movie
Voucher
```

Nên map sang DTO để kiểm soát field trả về.

---

## 15. Structure đề xuất cho dự án Cinema React

```txt
src/
├── main.jsx
├── App.jsx
│
├── shared/
│   ├── api/
│   │   └── httpClient.js
│   ├── layouts/
│   │   ├── AdminLayout.jsx
│   │   ├── CashierLayout.jsx
│   │   └── CustomerLayout.jsx
│   ├── components/
│   │   ├── Loading.jsx
│   │   ├── ErrorMessage.jsx
│   │   ├── ConfirmModal.jsx
│   │   └── Pagination.jsx
│   └── utils/
│       ├── formatCurrency.js
│       └── buildImageUrl.js
│
└── features/
    ├── concessions/
    │   ├── api/
    │   │   └── concessionApi.js
    │   ├── admin/
    │   │   ├── components/
    │   │   │   ├── ConcessionTable.jsx
    │   │   │   └── ConcessionPagination.jsx
    │   │   ├── pages/
    │   │   │   └── ConcessionListPage.jsx
    │   │   └── styles/
    │   │       └── concession-admin.css
    │   └── cashier/
    │       ├── components/
    │       │   ├── CashierConcessionCard.jsx
    │       │   └── CashierCart.jsx
    │       ├── hooks/
    │       │   └── useCashierCart.js
    │       ├── pages/
    │       │   └── CashierConcessionPage.jsx
    │       └── styles/
    │           └── concession-cashier.css
    │
    ├── movies/
    ├── vouchers/
    ├── rooms/
    ├── tickets/
    ├── payments/
    └── users/
```

---

## 16. Bảng tóm tắt quy tắc đặt tên

| Loại file/thư mục | Quy tắc | Ví dụ |
|---|---|---|
| Component | `PascalCase.jsx` | `ConcessionTable.jsx` |
| Page | `PascalCase.jsx` + hậu tố `Page` | `ConcessionListPage.jsx` |
| Layout | `PascalCase.jsx` + hậu tố `Layout` | `AdminLayout.jsx` |
| Hook | `useXxx.js` | `useCashierCart.js` |
| API file React | `camelCase.js` | `concessionApi.js` |
| Utility | `camelCase.js` | `formatCurrency.js` |
| CSS | `kebab-case.css` | `concession-admin.css` |
| Folder | chữ thường hoặc `kebab-case` | `features`, `cashier-concessions` |
| Backend Request DTO | `XxxRequestDTO.java` | `ConcessionRequestDTO.java` |
| Backend Response DTO | `XxxResponseDTO.java` hoặc `XxxListItemDTO.java` | `ConcessionListItemDTO.java` |

---

## 17. Quy tắc cho team khi tạo file mới

Trước khi tạo file mới, cần tự hỏi:

1. File này thuộc feature nào?
2. File này dùng riêng cho admin, cashier, customer hay dùng chung?
3. File này là page, component, hook, api, style hay utility?
4. Tên file có nói rõ chức năng không?
5. File có bị trùng nhiệm vụ với file đã có không?

Ví dụ muốn tạo bảng concession cho admin:

```txt
Feature: concessions
Role: admin
Type: component
Tên file: ConcessionTable.jsx
Đường dẫn: features/concessions/admin/components/ConcessionTable.jsx
```

Ví dụ muốn tạo hàm gọi API concession:

```txt
Feature: concessions
Type: api
Tên file: concessionApi.js
Đường dẫn: features/concessions/api/concessionApi.js
```

---

## 18. Quy tắc không nên vi phạm

Không đặt tên file bằng tiếng Việt không dấu hoặc viết tắt khó hiểu.

Không nên:

```txt
bapnuoc.jsx
dsphim.jsx
qlve.jsx
ctkm.jsx
abc.jsx
temp.jsx
test.jsx
```

Nên đặt:

```txt
ConcessionListPage.jsx
MovieListPage.jsx
TicketListPage.jsx
VoucherListPage.jsx
```

Không tạo file CSS riêng nếu CSS global đã đủ.

Không sửa CSS global nếu chỉ một màn hình cần style riêng.

Không để API call rải rác trong nhiều page nếu API đó dùng lại nhiều nơi.

Không trả Entity trực tiếp từ backend API nếu frontend chỉ cần một vài field.

---

## 19. Kết luận

Quy tắc chính:

```txt
Cái gì dùng chung → shared/
Cái gì thuộc nghiệp vụ riêng → features/<feature>/
Cái gì riêng theo vai trò → admin/ cashier/ customer/
File render UI → PascalCase.jsx
File gọi API/helper → camelCase.js
Hook → useXxx.js
CSS → kebab-case.css
DTO backend → RequestDTO / ResponseDTO
```

Nếu team giữ đúng quy tắc này, project sẽ dễ mở rộng, dễ review code, dễ chia việc trên GitHub và dễ viết tài liệu đặc tả.

---

## 20. Quy tac doc skill truoc khi lam task

Truoc moi yeu cau, can doc danh sach skill trong cac duong dan sau:

```txt
.agents/skills/
/.agents/skills
```

Sau do chon skill phu hop voi task dang lam va doc file `SKILL.md` cua skill do truoc khi phan tich hoac sua code.

Vi du skill thuong dung:

```txt
api-and-interface-design
frontend-ui-engineering
deprecation-and-migration
planning-and-task-breakdown
caveman
```

Khong bo qua buoc chon skill, vi day la buoc giu dung quy trinh, kien truc va style code cua project.
