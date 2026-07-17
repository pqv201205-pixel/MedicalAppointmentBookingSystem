**# 🩺 Hệ Thống Đặt Lịch Khám Bệnh - Doctor Appointment Booking System**



Hệ thống hỗ trợ quản lý và đặt lịch khám bệnh trực tuyến giúp kết nối Bệnh nhân và Bác sĩ một cách nhanh chóng, tối ưu hóa quy trình khám chữa bệnh của các phòng khám và bệnh viện.





**## 🚀 Công Nghệ Sử Dụng (Tech Stack)**



\### Backend (Spring Boot)

\*   Framework: Spring Boot 3.3.x, Spring Security, Spring Data JPA

\*   Java Version: Java 17

\*   Database: Microsoft SQL Server

\*   Caching \& Session: Redis (Lettuce client)

\*   Message Broker: RabbitMQ (Xử lý hàng đợi gửi Mail/Notification không đồng bộ)

\*   Security: JSON Web Token (JWT) cho Stateless Authentication (Xác thực không lưu trạng thái)

\*   Documentation: Springdoc OpenAPI (Swagger UI)

\*   Thư viện hỗ trợ: Lombok, Jakarta Validation, ZXing (Tạo QR Code cho lịch hẹn)



\### Frontend (React)

\*   Library: ReactJS, React Router DOM v6

\*   Language: TypeScript (Đảm bảo Type-Safe đồng bộ với DTO Backend)

\*   HTTP Client: Axios (Sử dụng Interceptors để tự động đính kèm JWT Token vào Header)





**## ✨ Tính Năng Chính (Key Features)**



\### 👥 Phân hệ Bệnh nhân (Patient)

\*   Đăng ký / Đăng nhập: Xác thực bảo mật dựa trên cơ chế JWT Token.

\*   Quản lý hồ sơ cá nhân: Xem và cập nhật thông tin y tế, tiểu sử bệnh lý cá nhân (`/patients/me`).

\*   Tìm kiếm bác sĩ: Xem danh sách bác sĩ, lọc theo chuyên khoa (`Specialization`), năm kinh nghiệm hoặc chi phí khám.

\*   Đặt lịch hẹn: Chọn bác sĩ, ngày giờ khám linh hoạt theo lịch trình trống của bác sĩ.

\*   Theo dõi lịch hẹn: 

&#x20;   \*   Xem danh sách lịch hẹn sắp diễn ra (`Upcoming Appointments`).

&#x20;   \*   Xem lịch sử toàn bộ các ca đã khám hoặc bị hủy (`Appointment History`).

\*   Nhận mã QR: Tự động sinh mã QR chứa thông tin lịch hẹn giúp check-in nhanh tại phòng khám.



\### 🥼 Phân hệ Bác sĩ (Doctor)

\*   Quản lý lịch trình làm việc cá nhân (Cấu hình khung giờ trống nhận khách).

\*   Theo dõi danh sách bệnh nhân đã đặt lịch hẹn theo ngày/tuần.

\*   Cập nhật trạng thái lịch hẹn, ghi chú bệnh án và kê đơn thuốc sau khi khám xong.



\### 👑 Phân hệ Quản trị viên (Admin)

\*   Quản lý danh sách người dùng (`Users`), Bác sĩ (`Doctors`) và Bệnh nhân (`Patients`).

\*   Thêm, sửa, xóa danh mục các chuyên khoa lâm sàng.

\*   Giám sát và thống kê số lượng ca khám toàn hệ thống.





\## 📂 Cấu Trúc Thư Mục Backend (Spring Boot)



```text

com.example.demo

├── Controllers     # Tiếp nhận \& điều hướng API (REST Endpoints)

├── Entities        # Thực thể JPA mapping trực tiếp xuống các bảng SQL Server

├── Repositories    # Tầng giao tiếp Database (Spring Data JPA)

├── Services        # Tầng xử lý logic nghiệp vụ (Interface \& Impl)

├── DTOs            # Các Data Transfer Object đóng gói dữ liệu Request/Response

│   ├── RequestDTO  # Dữ liệu Frontend gửi lên (Ví dụ: PatientRequest)

│   └── ResponseDTO # Dữ liệu Backend trả về (Ví dụ: PatientResponse, ApiResponse)

├── Security        # Cấu hình Spring Security, JWT Filter, CustomUserDetails

└── Exceptions      # Quản lý và định dạng lỗi tập trung (GlobalExceptionHandler)



\## 📂 Cấu Trúc Thư Mục Frontend (React + Vite + TypeScript)



```text

frontend

├── src                 # Thư mục chứa mã nguồn chính của ứng dụng

│   ├── components      # Các UI Component tái sử dụng (Button, Navbar, Modal...)

│   ├── pages           # Giao diện các trang chức năng (Patient, Doctor, Admin, Auth)

│   ├── services        # Tần giao tiếp API, cấu hình Axios kết nối đến Spring Boot

│   ├── store           # Quản lý trạng thái toàn cục (ví dụ: Redux Toolkit hoặc Zustand nếu có)

│   ├── types           # Định nghĩa các kiểu dữ liệu và Interface TypeScript (Đồng bộ với DTO)

│   ├── utils           # Các hàm tiện ích dùng chung (Format ngày tháng, tiền tệ, helper...)

│   ├── App.tsx         # Component gốc thiết lập Routes và bọc Context Providers

│   ├── index.css       # File cấu hình định dạng style (chứa chỉ thị Tailwind CSS)

│   └── main.tsx        # Điểm khởi chạy (Entry point) để render ứng dụng lên DOM

├── index.html          # File HTML gốc của ứng dụng Single Page Application (SPA)

├── package.json        # Khai báo các thư viện phụ thuộc (Dependencies) và các script chạy dự án

├── tailwind.config.js  # File cấu hình tùy chỉnh giao diện của Tailwind CSS

├── vite.config.ts      # File cấu hình các thông số hệ thống của công cụ bundler Vite

└── tsconfig.json       # Cấu hình các thiết lập biên dịch cho TypeScript





🛠️ Hướng Dẫn Cài Đặt \& Chạy Dự Án (Setup Guide)

**1. Yêu cầu hệ thống (Prerequisites)Để chạy được dự án này, máy tính của bạn cần cài đặt sẵn:**

Java SDK 17 hoặc cao hơn.

Apache Maven.

Microsoft SQL Server (Đang chạy tại cổng mặc định 1433).

Redis Server (Cổng mặc định 6379).

RabbitMQ Server (Cổng mặc định 5672).

Node.js và npm (Để chạy ứng dụng React).

**2. Triển khai cấu hình Backend (Spring Boot)**

Mở mã nguồn Backend bằng các IDE như IntelliJ IDEA hoặc Eclipse.

Tìm đến file cấu hình cấu trúc database src/main/resources/application.properties và sửa lại thông tin kết nối SQL Server của bạn:

Propertiesspring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=YourDatabaseName;encrypt=true;trustServerCertificate=true;

spring.datasource.username=tên\_tài\_khoản\_sql

spring.datasource.password=mật\_khẩu\_sql

\# Cấu hình Redis

spring.data.redis.host=localhost

spring.data.redis.port=6379

\# Cấu hình RabbitMQ

spring.rabbitmq.host=localhost

spring.rabbitmq.port=5672



Mở Terminal tại thư mục gốc của backend và chạy lệnh sau để khởi động dự án:mvn spring-boot:run

Backend sẽ chạy tại địa chỉ: http://localhost:80803. 





**3. Triển khai cấu hình Frontend (React)**

Di chuyển vào thư mục chứa mã nguồn Frontend bằng Terminal: cd path/to/your/react-project

Cài đặt toàn bộ các thư viện (dependencies) được khai báo trong package.json: npm install

Kiểm tra file cấu hình Axios (thường là api.ts hoặc axios.ts), đảm bảo biến baseURL đang trỏ đúng về cổng của Backend Spring Boot:TypeScriptbaseURL: 'http://localhost:8080/api'

Khởi chạy giao diện môi trường phát triển: npm run dev # hoặc npm start tùy thuộc vào dự án dùng Vite hay CRA

Frontend sẽ chạy tại địa chỉ: http://localhost:3000 (hoặc 5173 đối với Vite)



