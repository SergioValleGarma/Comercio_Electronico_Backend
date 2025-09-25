# 🚴‍♂️ Mundo Bici – E-commerce

> Tienda virtual de bicicletas con catálogo filtrable, carrito de compras y generación/consulta de órdenes desarrollada con Spring Boot y arquitectura MVC.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-red)](https://maven.apache.org/download.cgi)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13%2B-blue)](https://www.postgresql.org/download/)

## ✨ Características

- 🛒 **Carrito de compras** interactivo con gestión de sesiones
- 📦 **Catálogo filtrable** por categoría, precio y disponibilidad
- 🔍 **Búsqueda** de productos por nombre
- 📋 **Sistema de órdenes** completo con estados
- 👥 **Autenticación de usuarios** con roles (ADMIN/CUSTOMER)
- 🎨 **Interfaz web** con Thymeleaf y JavaScript
- 🌐 **API REST** completa para integraciones

## 🚀 Requisitos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java 17** - [Descargar aquí](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  ```bash
  java -version  # Verificar instalación
  ```

- 📦 **Maven 3.8+** - [Descargar aquí](https://maven.apache.org/download.cgi)
  ```bash
  mvn -v  # Verificar instalación
  ```

- 🐘 **PostgreSQL 13+** - [Descargar aquí](https://www.postgresql.org/download/)

## ⚡ Pasos para ejecutar

### 1. Clonar el proyecto
```bash
git clone https://github.com/SergioValleGarma/Comercio_Electronico_Backend.git
cd Comercio_Electronico_Backend
```

### 2. Configurar la base de datos
Conecta a PostgreSQL y ejecuta:
```sql
CREATE DATABASE ecomerce;
```

### 3. Configurar la conexión
Edita el archivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecomerce
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 4. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 5. Acceder a la aplicación
Abre tu navegador en: **[http://localhost:8080/products](http://localhost:8080/products)**

¡Listo! 🎉 La aplicación estará ejecutándose con datos de prueba incluidos.

## 📁 Estructura del proyecto

```
Comercio_Electronico_Backend/
├─ .idea/                    # 💡 Configuración IntelliJ IDEA
├─ .mvn/                     # 📦 Maven Wrapper
├─ src/
│  ├─ main/
│  │  ├─ java/org/example/ecomerce/
│  │  │  ├─ config/          # ⚙️ Configuraciones Spring
│  │  │  ├─ controller/      # 🌐 Controladores REST y Web
│  │  │  ├─ dto/             # 📋 Data Transfer Objects
│  │  │  ├─ interceptor/     # 🔄 Interceptores HTTP
│  │  │  ├─ model/           # 🏗️ Entidades JPA
│  │  │  │  ├─ Product.java     # Entidad Producto
│  │  │  │  ├─ Category.java    # Entidad Categoría
│  │  │  │  ├─ Order.java       # Entidad Orden
│  │  │  │  └─ OrderItem.java   # Item de orden
│  │  │  ├─ repository/      # 🗄️ Repositorios JPA
│  │  │  ├─ service/         # ⚡ Lógica de negocio
│  │  │  └─ EcomerceApplication.java  # 🚀 Clase principal
│  │  └─ resources/
│  │     ├─ application.properties  # ⚙️ Configuración
│  │     └─ data.sql         # 🌱 Datos de prueba iniciales
│  └─ test/                  # 🧪 Pruebas unitarias
├─ target/                   # 🎯 Archivos compilados
├─ .gitattributes           # 📝 Configuración Git
├─ .gitignore               # 🚫 Archivos ignorados por Git
├─ mvnw                     # 🐧 Maven Wrapper (Linux/Mac)
├─ mvnw.cmd                 # 🪟 Maven Wrapper (Windows)
├─ pom.xml                  # 📋 Configuración Maven
└─ README.md                # 📖 Documentación del proyecto
```

## 📚 Documentación de Endpoints API

### Información General
- **Base URL**: `http://localhost:8080/api`
- **Formato**: JSON
- **Autenticación**: Sesiones HTTP (cookies automáticas)

---

## 🔐 Autenticación

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
   "email": "admin@ecommerce.com", 
   "password": "password123" 
}
```

**Respuesta exitosa:**
```json
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
```

### Verificar Sesión
```http
GET /api/auth/session
```

### Cerrar Sesión
```http
POST /api/auth/logout
```

---

## 📦 Productos

### Listar Productos
```http
GET /api/products
```

**Parámetros opcionales:**
- `page` - Número de página (default: 0)
- `size` - Items por página (default: 12)
- `q` - Texto de búsqueda
- `category` - ID de categoría
- `priceMin` - Precio mínimo
- `priceMax` - Precio máximo
- `inStock` - Filtrar por disponibilidad (true/false)
- `sort` - Ordenamiento: `priceAsc`, `priceDesc`, `nameAsc`, `nameDesc`

**Ejemplos:**
```http
GET /api/products?page=0&size=12
GET /api/products?category=1&priceMin=10&priceMax=100
GET /api/products?q=bicicleta&inStock=true&sort=priceAsc
```

### Obtener Producto por ID
```http
GET /api/products/{id}
```

---

## 🛒 Carrito de Compras

### Ver Carrito
```http
GET /api/cart
```

### Agregar al Carrito
```http
POST /api/cart/add?productId=1&quantity=2
```

### Actualizar Cantidad
```http
POST /api/cart/update?productId=1&quantity=3
```

### Remover del Carrito
```http
DELETE /api/cart/remove/1
```

### Limpiar Carrito
```http
DELETE /api/cart/clear
```

### Información del Carrito
```http
GET /api/cart/info
```

---

## 📋 Órdenes

### Crear Orden
```http
POST /api/orders
Content-Type: application/json

{
    "cartItems": [
        {
            "productId": 1,
            "productName": "Bicicleta Montaña",
            "productPrice": 1200.00,
            "quantity": 2,
            "subtotal": 2400.00
        }
    ],
    "shippingAddress": "Av. Principal 123, Lima",
    "phoneNumber": "+51 987654321",
    "notes": "Entregar después de las 5pm"
}
```

### Listar Órdenes del Usuario
```http
GET /api/orders?page=0&size=10&status=PENDING
```

### Ver Detalle de Orden
```http
GET /api/orders/{id}
```

### Cancelar Orden
```http
POST /api/orders/{id}/cancel
```

### Ver Estado de Orden
```http
GET /api/orders/{id}/status
```

### Estadísticas del Usuario
```http
GET /api/orders/statistics/user
```

---

## 👥 Administración (Rol ADMIN requerido)

### Listar Usuarios
```http
GET /api/users
```

### Crear Usuario
```http
POST /api/users
Content-Type: application/json

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
```

### Listar Todas las Órdenes
```http
GET /api/admin/orders?page=0&size=20&status=PENDING
```

### Actualizar Estado de Orden
```http
PATCH /api/admin/orders/{id}/status?status=SHIPPED
```

### Estadísticas Generales
```http
GET /api/admin/orders/statistics
```

**Respuesta:**
```json
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
```

---

## 📂 Categorías

### Listar Categorías
```http
GET /api/categories
```

---

## 📊 Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | ✅ OK - Solicitud exitosa |
| 201 | ✅ Created - Recurso creado |
| 400 | ❌ Bad Request - Datos inválidos |
| 401 | ❌ Unauthorized - No autenticado |
| 403 | ❌ Forbidden - Sin permisos |
| 404 | ❌ Not Found - Recurso no existe |
| 500 | ❌ Internal Server Error |

## 🔄 Flujos Típicos de Uso

### Flujo de Compra (Cliente):
1. `GET /products` - Buscar productos
2. `POST /cart/add` - Agregar al carrito
3. `GET /cart` - Revisar carrito
4. `POST /orders` - Crear orden
5. `GET /orders` - Ver mis órdenes

### Flujo de Administración:
1. `GET /admin/orders` - Ver todas las órdenes
2. `PATCH /admin/orders/{id}/status` - Actualizar estado
3. `GET /admin/orders/statistics` - Ver estadísticas
4. `GET /users` - Gestionar usuarios

## ⚙️ Configuración para Frontend

### Headers Recomendados:
```javascript
{
   'Content-Type': 'application/json',
   'Accept': 'application/json'
}
```

### Manejo de Errores:
Todos los endpoints retornan formato estándar:
```json
{
   "success": false,
   "message": "Mensaje de error descriptivo",
   "data": null
}
```

### CORS Configurado para:
- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)
- `http://localhost:8080` (Backend)

---

## 🛠️ Desarrollo

### Ejecutar en modo desarrollo:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Ver logs:
```bash
tail -f logs/application.log
```

---

Desarrollado con ❤️ por **Sergio Valle Garma**