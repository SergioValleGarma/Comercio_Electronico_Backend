# Mundo Bici – E-commerce (MVC + Spring Boot)
Tienda virtual de bicicletas con catálogo filtrable, carrito de compras y generación/consulta de órdenes.

## Requisitos
- Java 17 (java -version) [link de descarga](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Maven 3.8+ (mvn -v) [link de descarga](https://maven.apache.org/download.cgi)
- PostgreSQL 13+ (local o vía Docker) [link de descarga](https://www.postgresql.org/download/)

## Pasos para ejecutar
1. Clonar el proyecto
```bash
git clone https://github.com/SebastianLl28/ecomerce-mvc.git
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
