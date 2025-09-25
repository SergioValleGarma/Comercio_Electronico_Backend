# Mundo Bici – E-commerce (MVC + Spring Boot)
Tienda virtual de bicicletas con catálogo filtrable, carrito de compras y generación/consulta de órdenes.

## Requisitos
- Java 17 (java -version) [link de descarga](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Maven 3.8+ (mvn -v) [link de descarga](https://maven.apache.org/download.cgi)
- PostgreSQL 13+ (local o vía Docker) [link de descarga](https://www.postgresql.org/download/)

## Pasos para ejecutar
1. Clonar el proyecto
```bash
git clone https://github.com/SergioValleGarma/Comercio_Electronico_Backend.git
````
2. Crear la base de datos
```sql
CREATE DATABASE ecomerce;
```
3. Configurar la conexión a la base de datos en `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecomerce
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```
4. Ejecutar la aplicación
```bash
cd ecomerce-mvc
mvn spring-boot:run
```
5. Acceder a la aplicación en el navegador
6. Abrir [http://localhost:8080/products](http://localhost:8080/products)

## Estructura del proyecto
````
src/
 └─ main/
    ├─ java/org/example/ecomerce/
    │  ├─ controller/        # Controladores web (Thymeleaf)
    │  ├─ model/             # Entidades JPA (Product, Category, Order, OrderItem)
    │  ├─ repository/        # Repositorios JPA (ProductRepository, ...)
    │  └─ service/           # Reglas de negocio (ProductService, OrderService, ...)
    └─ resources/
       ├─ static/
       │  ├─ css/            # Estilos
       │  └─ js/             # JS (toastr, carrito)
       ├─ templates/
       │  ├─ cart/           # Vistas de carrito
       │  ├─ orders/         # Vistas de órdenes
       │  └─ products.html   # Catálogo + filtros
       ├─ application.properties
       └─ data.sql           # Semillas de datos
````
## Documentación de Endpoints API - Ecommerce Backend 📋
Información General
Base URL: http://localhost:8080/api

Formato: JSON

Autenticación: Sesiones HTTP (cookies automáticas)

🔐 Autenticación
1. Login
POST /auth/login

Request Body:

json
{
   "email": "admin@ecommerce.com", 
   "password": "password123" 
}
Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "userId": 1,
        "email": "usuario@ejemplo.com",
        "fullName": "Juan Pérez",
        "role": "CUSTOMER",
        "message": "Login exitoso"
    }
}
2. Verificar Sesión
GET /auth/session

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "userId": 1,
        "email": "usuario@ejemplo.com",
        "fullName": "Juan Pérez",
        "role": "CUSTOMER",
        "message": "Sesión activa"
    }
}
3. Logout
POST /auth/logout

Response:

json
{
    "success": true,
    "message": "Sesión cerrada exitosamente",
    "data": null
}
📦 Productos
4. Listar Productos
GET /products

Query Parameters:

page (opcional): Número de página (default: 0)

size (opcional): Items por página (default: 12)

q (opcional): Texto de búsqueda

category (opcional): ID de categoría

priceMin (opcional): Precio mínimo

priceMax (opcional): Precio máximo

inStock (opcional): true/false para stock disponible

sort (opcional): priceAsc, priceDesc, nameAsc, nameDesc

Ejemplos:

GET /products?page=0&size=12

GET /products?category=1&priceMin=10&priceMax=100

GET /products?q=laptop&inStock=true&sort=priceAsc

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "content": [
            {
                "id": 1,
                "name": "Laptop Gaming",
                "description": "Laptop para gaming de alto rendimiento",
                "price": 1200.00,
                "stock": 15,
                "imageUrl": "/images/laptop.jpg",
                "categoryId": 1,
                "categoryName": "Electrónicos"
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 12,
            "sort": []
        },
        "totalPages": 5,
        "totalElements": 48,
        "last": false,
        "first": true,
        "empty": false
    }
}
5. Obtener Producto por ID
GET /products/{id}

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "id": 1,
        "name": "Laptop Gaming",
        "description": "Laptop para gaming de alto rendimiento",
        "price": 1200.00,
        "stock": 15,
        "imageUrl": "/images/laptop.jpg",
        "categoryId": 1,
        "categoryName": "Electrónicos"
    }
}
🛒 Carrito de Compras
6. Ver Carrito
GET /cart

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": [
        {
            "productId": 1,
            "productName": "Laptop Gaming",
            "productPrice": 1200.00,
            "quantity": 2,
            "subtotal": 2400.00
        }
    ]
}
7. Agregar al Carrito
POST /cart/add?productId=1&quantity=2

Query Parameters:

productId (requerido): ID del producto

quantity (opcional): Cantidad (default: 1)

Response:

json
{
    "success": true,
    "message": "Producto agregado al carrito",
    "data": {
        "cartItems": [
            {
                "productId": 1,
                "productName": "Laptop Gaming",
                "productPrice": 1200.00,
                "quantity": 2,
                "subtotal": 2400.00
            }
        ],
        "total": 2400.00,
        "itemCount": 2,
        "message": "Producto agregado al carrito"
    }
}
8. Actualizar Cantidad
POST /cart/update?productId=1&quantity=3

Response:

json
{
    "success": true,
    "message": "Cantidad actualizada",
    "data": {
        "cartItems": [
            {
                "productId": 1,
                "productName": "Laptop Gaming",
                "productPrice": 1200.00,
                "quantity": 3,
                "subtotal": 3600.00
            }
        ],
        "total": 3600.00,
        "itemCount": 3,
        "message": "Cantidad actualizada"
    }
}
9. Remover del Carrito
DELETE /cart/remove/1

Response:

json
{
    "success": true,
    "message": "Producto eliminado del carrito",
    "data": {
        "cartItems": [],
        "total": 0.00,
        "itemCount": 0,
        "message": "Producto eliminado del carrito"
    }
}
10. Limpiar Carrito
DELETE /cart/clear

Response:

json
{
    "success": true,
    "message": "Carrito vaciado exitosamente",
    "data": null
}
11. Información del Carrito
GET /cart/info

Response:

json
{
    "success": true,
    "message": "Información del carrito",
    "data": {
        "cartItems": [
            {
                "productId": 1,
                "productName": "Laptop Gaming",
                "productPrice": 1200.00,
                "quantity": 2,
                "subtotal": 2400.00
            }
        ],
        "total": 2400.00,
        "itemCount": 2,
        "message": "Información del carrito"
    }
}
📋 Órdenes
12. Crear Orden
POST /orders

Request Body:

json
{
    "cartItems": [
        {
            "productId": 1,
            "productName": "Laptop Gaming",
            "productPrice": 1200.00,
            "quantity": 2,
            "subtotal": 2400.00
        }
    ],
    "shippingAddress": "Av. Principal 123, Lima",
    "phoneNumber": "+51 987654321",
    "notes": "Entregar después de las 5pm"
}
Response:

json
{
    "success": true,
    "message": "Orden creada exitosamente",
    "data": {
        "id": 1001,
        "userId": 1,
        "totalAmount": 2400.00,
        "status": "PENDING",
        "orderDate": "2024-01-15T14:30:00Z",
        "shippingAddress": "Av. Principal 123, Lima",
        "phoneNumber": "+51 987654321",
        "notes": "Entregar después de las 5pm",
        "orderItems": [
            {
                "id": 2001,
                "productId": 1,
                "productName": "Laptop Gaming",
                "quantity": 2,
                "unitPrice": 1200.00,
                "subtotal": 2400.00
            }
        ]
    }
}
13. Listar Órdenes del Usuario
GET /orders

Query Parameters:

page (opcional): Número de página (default: 0)

size (opcional): Items por página (default: 10)

status (opcional): Filtrar por estado

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "content": [
            {
                "id": 1001,
                "userId": 1,
                "totalAmount": 2400.00,
                "status": "PENDING",
                "orderDate": "2024-01-15T14:30:00Z",
                "shippingAddress": "Av. Principal 123, Lima"
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 10
        },
        "totalPages": 2,
        "totalElements": 8,
        "last": false,
        "first": true
    }
}
14. Ver Detalle de Orden
GET /orders/{id}

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "id": 1001,
        "userId": 1,
        "totalAmount": 2400.00,
        "status": "PENDING",
        "orderDate": "2024-01-15T14:30:00Z",
        "shippingAddress": "Av. Principal 123, Lima",
        "phoneNumber": "+51 987654321",
        "notes": "Entregar después de las 5pm",
        "orderItems": [
            {
                "id": 2001,
                "productId": 1,
                "productName": "Laptop Gaming",
                "quantity": 2,
                "unitPrice": 1200.00,
                "subtotal": 2400.00
            }
        ]
    }
}
15. Cancelar Orden
POST /orders/{id}/cancel

Response:

json
{
    "success": true,
    "message": "Orden cancelada exitosamente",
    "data": null
}
16. Ver Estado de Orden
GET /orders/{id}/status

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "status": "PENDING",
        "statusDisplayName": "Pendiente"
    }
}
17. Estadísticas del Usuario
GET /orders/statistics/user

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "totalOrders": 5,
        "totalSpent": 4500.00
    }
}
👥 Usuarios (Requieren rol ADMIN)
18. Listar Usuarios
GET /users

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": [
        {
            "id": 1,
            "firstName": "Admin",
            "lastName": "User",
            "email": "admin@example.com",
            "role": "ADMIN",
            "active": true,
            "fullName": "Admin User"
        }
    ]
}
19. Crear Usuario
POST /users

Request Body:

json
{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "password": "password123",
    "phone": "+51 987654321",
    "address": "Av. Principal 123",
    "role": "CUSTOMER",
    "active": true
}
20. Actualizar Usuario
PUT /users/{id}

21. Eliminar Usuario
DELETE /users/{id}

⚙️ Administración (Requieren rol ADMIN)
22. Listar Todas las Órdenes
GET /admin/orders

Query Parameters:

page (opcional): Número de página (default: 0)

size (opcional): Items por página (default: 20)

status (opcional): Filtrar por estado

23. Actualizar Estado de Orden
PATCH /admin/orders/{id}/status?status=SHIPPED

24. Estadísticas Generales
GET /admin/orders/statistics

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": {
        "totalOrders": 45,
        "weeklyOrders": 8,
        "monthlyOrders": 25,
        "pendingOrders": 3,
        "weeklyRevenue": 5600.00,
        "monthlyRevenue": 25000.00,
        "averageOrderValue": 544.44
    }
}
📂 Categorías
25. Listar Categorías
GET /categories

Response:

json
{
    "success": true,
    "message": "Operación exitosa",
    "data": [
        {
            "id": 1,
            "name": "Electrónicos",
            "description": "Dispositivos electrónicos"
        },
        {
            "id": 2,
            "name": "Ropa",
            "description": "Prendas de vestir"
        }
    ]
}
📊 Códigos de Estado HTTP
Código	Descripción
200	OK - Solicitud exitosa
201	Created - Recurso creado
400	Bad Request - Datos inválidos
401	Unauthorized - No autenticado
403	Forbidden - Sin permisos
404	Not Found - Recurso no existe
500	Internal Server Error - Error del servidor
🔄 Flujos Típicos
Flujo de Compra:
GET /products - Buscar productos

POST /cart/add - Agregar al carrito

GET /cart - Ver carrito

POST /orders - Crear orden

GET /orders - Ver mis órdenes

Flujo de Administración:
GET /admin/orders - Ver todas las órdenes

PATCH /admin/orders/{id}/status - Actualizar estado

GET /admin/orders/statistics - Ver estadísticas

GET /users - Gestionar usuarios

⚙️ Configuración Frontend
Headers Recomendados:
javascript
{
   'Content-Type': 'application/json',
   'Accept': 'application/json'
}
Manejo de Errores:
Todos los endpoints retornan formato estándar:

json
{
   "success": false,
   "message": "Mensaje de error descriptivo",
   "data": null
}
CORS Configurado para:
http://localhost:3000 (React)

http://localhost:5173 (Vite)

http://localhost:8080 (Backend)

