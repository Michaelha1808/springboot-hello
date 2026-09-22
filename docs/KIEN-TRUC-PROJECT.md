# Kiến trúc project Spring Boot – giải thích cho người mới

Tài liệu này giải thích **tại sao** project chia thư mục như hiện tại, mỗi thư mục / file để làm gì, và trả lời câu hỏi lớn nhất: *tại sao `service` và `repository` lại có cả interface lẫn thư mục `impl/`?*

Mọi ví dụ đều lấy từ code thật trong project này.

---

## 1. Luồng đi của một request

Khi bạn gọi `GET http://localhost:8003/v1/api/products`, request đi qua các tầng như sau:

```
Trình duyệt / Postman
        │  GET /v1/api/products
        ▼
┌──────────────────────┐
│  ProductController   │  ← tầng CONTROLLER: nhận HTTP, biết URL nào gọi hàm nào
└──────────┬───────────┘
           │ productService.findAllProducts()
           ▼
┌──────────────────────┐
│  ProductServiceImpl  │  ← tầng SERVICE: logic nghiệp vụ (hiện tại chỉ "chuyển tiếp")
└──────────┬───────────┘
           │ productRepository.findAllProducts()
           ▼
┌──────────────────────┐
│ ProductRepositoryImpl│  ← tầng REPOSITORY: lấy dữ liệu (hiện tại trả dữ liệu giả)
└──────────┬───────────┘
           │ List<ProductEntity>
           ▼
   (Database – chưa có, sẽ thêm sau)

Sau đó dữ liệu quay ngược lên: Repository → Service → Controller → Spring tự chuyển thành JSON → trả về trình duyệt.
```

Quy tắc vàng: **mỗi tầng chỉ gọi tầng ngay dưới nó**. Controller không được gọi thẳng Repository.

---

## 2. Từng thư mục để làm gì

```
src/main/java/com/michaeljava/springboot_hello/
├── SpringbootHelloApplication.java   ← điểm khởi động
├── controller/                       ← nhận HTTP request
├── service/                          ← logic nghiệp vụ
│   └── impl/
├── repository/                       ← đọc/ghi dữ liệu
│   └── impl/
├── entity/                           ← class ánh xạ bảng trong DB
└── dto/                              ← dữ liệu trao đổi với client
    ├── request/
    └── response/
```

### `SpringbootHelloApplication.java` – điểm khởi động
Có `main()`, chạy `SpringApplication.run(...)`. Annotation `@SpringBootApplication` bảo Spring: *"quét toàn bộ package `com.michaeljava.springboot_hello` và các package con, tìm mọi class có `@RestController`, `@Service`, `@Repository`... rồi tạo object cho tôi"*. Vì vậy các thư mục con phải nằm **dưới** package này thì Spring mới tìm thấy.

### `controller/` – cửa ngõ HTTP
File: `controller/ProductController.java`

```java
@RestController
@RequestMapping("/v1/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")           // GET /v1/api/products
    public List<ProductEntity> getAllProducts(){
        return productService.findAllProducts();
    }
}
```

Nhiệm vụ: map URL → hàm, lấy tham số từ request, gọi Service, trả kết quả. **Không** chứa logic tính toán hay truy cập DB. Nếu bạn thấy mình viết `if/else` nghiệp vụ trong Controller → đó là dấu hiệu nên đẩy xuống Service.

### `service/` – logic nghiệp vụ
File: `service/ProductService.java` (interface) và `service/impl/ProductServiceImpl.java` (class thật).

Đây là nơi đặt các quy tắc của "bài toán": kiểm tra giá không được âm, tính giảm giá, gọi 2–3 repository rồi gộp kết quả, gửi email sau khi tạo đơn... Hiện tại `ProductServiceImpl` chỉ chuyển tiếp xuống repository vì project còn đơn giản – nhưng khi nghiệp vụ lớn lên, toàn bộ "trí thông minh" nằm ở đây.

### `repository/` – đọc / ghi dữ liệu
File: `repository/ProductRepository.java` (interface) và `repository/impl/ProductRepositoryImpl.java`.

Chỉ làm một việc: lấy dữ liệu vào / ra khỏi nơi lưu trữ (MySQL, PostgreSQL, MongoDB, file...). Không có logic nghiệp vụ.

Hiện tại project **chưa có database**, nên `ProductRepositoryImpl` trả dữ liệu giả (hard-code `"Michael java"`, giá `21.6`). Sau này khi thêm Spring Data JPA, bạn chỉ cần thay class này – các tầng trên không đổi (xem mục 3 để hiểu tại sao).

### `entity/` – ánh xạ bảng trong DB
File: `entity/ProductEntity.java`

Một Entity = một bảng. `ProductEntity` có `id`, `productName`, `productPrice` → tương ứng bảng `product` với 3 cột. Khi thêm JPA, class này sẽ được gắn `@Entity`, `@Table`, `@Id`...

`OrderEntity` và `OrderRepository` hiện đang rỗng – là chỗ chuẩn bị cho tính năng "đơn hàng" sau này.

### `dto/` – dữ liệu trao đổi với client
- `dto/request/product/ProductCreateRequest.java` – dữ liệu client **gửi lên** khi tạo sản phẩm (chỉ có `productName`, `productPrice`, không có `id` vì id do server sinh).
- `dto/response/product/ProductResponseVO.java` – dữ liệu server **trả về** (VO = Value Object, cách gọi khác của response DTO).

**Tại sao không dùng thẳng Entity cho API?**
1. Entity phản ánh cấu trúc DB. Nếu trả thẳng Entity, mọi cột (kể cả cột nhạy cảm như `password`, `createdBy`, cột nội bộ) đều lộ ra ngoài.
2. Khi đổi cấu trúc bảng, API cũng đổi theo → client bị vỡ. Có DTO ở giữa thì bảng đổi mà API vẫn giữ nguyên.
3. Request tạo mới không nên cho client tự đặt `id`.

> Ghi chú: hiện tại `ProductController` vẫn đang nhận và trả thẳng `ProductEntity`; hai class DTO đã tạo nhưng chưa dùng. Đây là chỗ bạn có thể cải thiện: Controller nhận `ProductCreateRequest`, Service chuyển sang `ProductEntity`, rồi trả về `ProductResponseVO`.

---

## 3. Tại sao có interface + thư mục `impl/`? (câu hỏi chính)

Nhìn vào repository:

```java
// repository/ProductRepository.java  ← INTERFACE: "hợp đồng", chỉ nói LÀM GÌ
public interface ProductRepository {
    ProductEntity createProduct(ProductEntity product);
    List<ProductEntity> findAllProducts();
}

// repository/impl/ProductRepositoryImpl.java  ← CLASS THẬT: nói LÀM NHƯ THẾ NÀO
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public List<ProductEntity> findAllProducts() {
        // ... code thật ở đây
    }
}
```

Interface giống như **bản mô tả công việc**: "ai làm ProductRepository thì phải biết `createProduct` và `findAllProducts`". Class trong `impl/` là **người thực sự làm việc đó**.

### Điểm mấu chốt: tầng trên chỉ biết interface

Nhìn `ProductServiceImpl`:

```java
@Autowired
private ProductRepository productRepository;   // kiểu là INTERFACE, không phải ProductRepositoryImpl
```

Service **không hề biết** `ProductRepositoryImpl` tồn tại. Nó chỉ biết "có một thứ gì đó tên ProductRepository, có hàm findAllProducts". Điều này mang lại 3 lợi ích:

**a) Đổi cách làm mà không đụng code tầng trên**

Hôm nay `ProductRepositoryImpl` trả dữ liệu giả. Tuần sau bạn viết `ProductRepositoryJpaImpl` đọc từ MySQL. Chỉ cần đổi class nào được gắn `@Repository` – `ProductServiceImpl` và `ProductController` **không sửa một dòng nào**. Nếu Service khai báo thẳng `private ProductRepositoryImpl productRepository;` thì bạn phải sửa Service mỗi lần đổi.

**b) Dễ viết test**

Khi test `ProductServiceImpl`, bạn không muốn cần DB thật. Với interface, bạn tạo một class giả (mock) implement `ProductRepository` trả dữ liệu cố định, rồi "nhét" vào Service. Không có interface → bắt buộc phải có DB mới test được.

**c) Spring tự nối dây (Dependency Injection)**

Cơ chế:
1. Lúc khởi động, Spring quét package, thấy `ProductRepositoryImpl` có `@Repository` → tạo 1 object (gọi là *bean*) và ghi nhớ "bean này implement ProductRepository".
2. Thấy `ProductServiceImpl` có `@Service` → tạo bean, thấy field `@Autowired ProductRepository` → tìm bean nào implement `ProductRepository` → nhét `ProductRepositoryImpl` vào.
3. Tương tự với `ProductController` ↔ `ProductService`.

Bạn **không bao giờ** viết `new ProductRepositoryImpl()`. Spring làm hộ. Đó là "Inversion of Control" – bạn không tự tạo object, để container tạo và đưa cho bạn.

### Có bắt buộc không?

Không. Với project nhỏ, 1 interface – 1 impl trông hơi "thừa". Nhưng đây là **convention rất phổ biến trong các công ty Java**, và khi project lớn (nhiều impl, nhiều test) bạn sẽ thấy giá trị. Học sớm để quen mắt khi đọc code người khác.

Quy tắc đặt tên thường gặp: interface `XxxService` → impl `XxxServiceImpl` trong package `impl/`.

---

## 4. Các annotation đang dùng trong project

| Annotation | Đặt ở đâu | Ý nghĩa |
|---|---|---|
| `@SpringBootApplication` | class main | Bật auto-config + quét package con tìm bean |
| `@RestController` | class Controller | Đây là controller, mọi hàm trả về được chuyển thành JSON |
| `@RequestMapping("/v1/api")` | class Controller | Prefix URL chung cho mọi hàm trong class |
| `@GetMapping("/products")` | hàm | Map HTTP GET `/v1/api/products` → hàm này |
| `@PostMapping("/product/add")` | hàm | Map HTTP POST → hàm này |
| `@Service` | class impl của service | Đánh dấu bean tầng service |
| `@Repository` | class impl của repository | Đánh dấu bean tầng dữ liệu (Spring còn tự dịch exception DB) |
| `@Autowired` | field | "Spring ơi, tìm bean phù hợp nhét vào đây" |
| `@Override` | hàm | Xác nhận hàm này implement từ interface (compiler kiểm tra giúp) |
| `@SpringBootTest` | class test | Khởi động toàn bộ Spring context khi chạy test |

> Ghi chú: hiện `@Service` và `@Repository` đang được đặt **cả trên interface** (`ProductService`, `ProductRepository`). Spring không tạo bean từ interface nên annotation ở đó không có tác dụng – chỉ cần đặt trên class impl là đủ. Xoá đi cũng không sao.

---

## 5. Cấu trúc project lớn hơn (từ ảnh bạn gửi)

Project e-commerce trong ảnh có thêm nhiều thư mục. Ý nghĩa từng cái:

| Thư mục | Để làm gì | Nên học khi nào |
|---|---|---|
| `models/` | = `entity/` của bạn, chỉ khác tên | – |
| `dto/requests`, `dto/responses`, `dto/vo` | = `dto/` của bạn. `vo` (Value Object) thường là object trả về cho client | – |
| `services/`, `repositories/`, `controllers/` | = 3 tầng của bạn (đặt tên số nhiều) | – |
| `exceptions/` | Định nghĩa exception riêng (`ProductNotFoundException`...) và `@ControllerAdvice` để bắt mọi lỗi, trả JSON lỗi thống nhất thay vì stack trace | **Sớm** |
| `apiresponse/` (`ApiResponse`, `ResponseUtils`) | Bọc mọi response về cùng một khuôn: `{ "code": 200, "message": "ok", "data": {...} }` | **Sớm** |
| `config/` | Các class `@Configuration`: cấu hình CORS, bean thủ công, Swagger, ObjectMapper... | Trung bình |
| `enums/` | Hằng số có kiểu: `OrderStatus.PENDING / PAID / CANCELLED` | Trung bình |
| `utils/` | Hàm tiện ích dùng chung (format ngày, sinh mã...) không thuộc tầng nào | Trung bình |
| `payload/` | Thường trùng vai trò với `dto/` – tuỳ team đặt tên | – |
| `security/` | Spring Security: JWT filter, phân quyền, mã hoá password | Sau |
| `aop/annotation`, `aop/aspect` | Aspect-Oriented Programming: viết annotation riêng (vd `@LogExecutionTime`) và Aspect tự chạy code trước/sau hàm (log, đo thời gian, kiểm tra quyền) mà không sửa hàm | Sau |

Gợi ý thứ tự học tiếp: `exceptions` + `apiresponse` trước (dùng ngay được, thấy hiệu quả rõ), sau đó `config`/`enums`, cuối cùng `security` và `aop`.

---

## 6. Cấu hình và profile

`src/main/resources/application.yaml`:

```yaml
server:
  port: 8001
spring:
  profiles:
    active: pro      # ← profile đang bật
```

**Profile** = bộ cấu hình cho từng môi trường. File `application-<tên>.yaml` sẽ được nạp *đè lên* `application.yaml` khi profile đó bật:

| Profile | File | Port |
|---|---|---|
| (mặc định) | `application.yaml` | 8001 |
| `dev` | `application-dev.yaml` | 8002 |
| `pro` | `application-pro.yaml` | 8003 |
| `test` | `application-test.yaml` | 8004 |

Vì `active: pro`, app hiện chạy ở **port 8003**, không phải 8001. Đổi profile khi chạy: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.

Thực tế profile hay dùng để tách thông tin DB, URL dịch vụ bên ngoài, mức log... giữa máy dev và server thật.

---

## 7. Những chỗ code hiện tại còn "chưa đúng" (bài tập tự sửa)

1. **Setter trong `ProductEntity` rỗng** – `setId`, `setProductName`, `setProductPrice` không gán gì cả, nên `GET /v1/api/products` sẽ trả `id: 0, productName: null, productPrice: null`. Cần gán `this.id = id;`... hoặc tốt hơn:
2. **Dùng Lombok** – `pom.xml` đã có Lombok. Gắn `@Data` (hoặc `@Getter @Setter`) lên `ProductEntity` và xoá setter tay → Lombok tự sinh getter/setter đúng.
3. **`createProduct` trong `ProductRepositoryImpl` return `null`** – nên trả về `product` (hoặc object vừa tạo).
4. **`POST /product/add` chưa có `@RequestBody`** – tham số `ProductEntity productEntity` cần `@RequestBody` để Spring đọc JSON từ body. Và nên đổi sang nhận `ProductCreateRequest` thay vì Entity (xem mục 2).
5. **`PlayerController` đang bị comment toàn bộ** – vì nó đọc `player.name/age/address` từ YAML mà YAML không còn các key này. Muốn dùng lại thì thêm vào `application.yaml`:
   ```yaml
   player:
     name: Michael
     age: 25
     address: Ha Noi
   ```
