# GameStore — Venta física de videojuegos en línea

Proyecto final de **Programación 1** (Universidad Libre, docente Andrés Castaño).
Aplicación Java de escritorio para vender videojuegos físicos (disco/cartucho) por internet: el cliente elige juegos, los
agrega al carrito, escribe su dirección y confirma la compra; el administrador gestiona el catálogo y marca los pedidos como enviados.

## Integrantes

| Nombre | Código |
|---|---|
| Jean _(apellido)_ | _(código)_ |
| _(integrante 2)_ | _(código)_ |

## Tecnologías

Java 17+ · Swing (interfaz gráfica) · SQLite (base de datos local) · Maven

## Instrucciones de ejecución

1. Instala **JDK 17 o superior** y **Maven** (o usa NetBeans / IntelliJ, que ya traen Maven).
2. Clona el repositorio:
   ```bash
   git clone <URL-del-repositorio>
   cd gamestore
   ```
3. Ejecuta:
   ```bash
   mvn compile exec:java
   ```
   *(En NetBeans: Archivo → Abrir proyecto → carpeta `gamestore` → clic derecho → Run. La clase principal es `app.Main`.)*
4. La **primera vez** el programa crea solo el archivo `gamestore.db` ejecutando el script `src/main/resources/schema.sql`
   (tablas + 4 videojuegos + administrador). No hay que instalar nada más.

**Usuario administrador de prueba:** `admin` / `admin123`
**Cliente:** pulsa **Registrarse** en la ventana de inicio.

**Para probar los descuentos (patrón Strategy):** 2 × *Elden Ring Edición Coleccionista* → 5 % por compras desde $150; 3 o más videojuegos cualesquiera → 10 %.

> Si aparece una advertencia de `SLF4J` en la consola, es del driver de SQLite y no afecta al programa.

## Base de datos

- Motor: SQLite (archivo local `gamestore.db`, ignorado por git).
- **Script de creación:** [`src/main/resources/schema.sql`](src/main/resources/schema.sql)
- Tablas: `usuario`, `videojuego`, `compra`, `detalle_compra`.

## Documentación

Descripción, diagrama de clases y modelo de datos: [`docs/Documentacion.md`](docs/Documentacion.md)

## Estructura de paquetes

| Paquete | Contenido |
|---|---|
| `app` | `Main`: punto de entrada |
| `modelo` | Clases del dominio: `Usuario`, `Cliente`, `Administrador`, `Videojuego`, `Carrito`, `Compra`… |
| `excepciones` | Excepciones propias del dominio |
| `bd` | `ConexionBD` (Singleton) |
| `dao` | Interfaces `Repositorio` y clases DAO que hablan con SQLite |
| `servicio` | Reglas del negocio: `AuthService`, `CatalogoService`, `CompraService` |
| `descuento` | Estrategias de descuento (patrón Strategy) |
| `fachada` | `TiendaFacade` (patrón Facade) |
| `vista` | Ventanas Swing: `LoginFrame`, `ClienteFrame`, `AdminFrame` |

---

## Mapa del proyecto

### Pilares y requisitos de POO

| Concepto | Dónde está | Cómo se usa |
|---|---|---|
| **Herencia** | `modelo.Cliente` y `modelo.Administrador` extienden `modelo.Usuario` | Ambos reutilizan nombre, usuario y contraseña; `Cliente` agrega la dirección de envío. Un Cliente *es un* Usuario. |
| **Polimorfismo** | `Usuario.getRol()` / `puedeAdministrar()` sobrescritos con `@Override` en `Cliente` y `Administrador`; `EstrategiaDescuento` en `descuento` | `LoginFrame.ingresar()` recibe un `Usuario` y llama `puedeAdministrar()` para abrir el panel correcto. `CompraService` llama `calcularDescuento()` sobre cualquier estrategia. |
| **Encapsulamiento** | Todas las clases de `modelo` | Atributos `private`, getters/setters; los setters validan (precio > 0, stock ≥ 0, textos no vacíos…) y lanzan `DatosInvalidosException` antes de cambiar el estado. |
| **Abstracción** | `modelo.Usuario` (`abstract`) con `getRol()` y `puedeAdministrar()` abstractos | Modela el concepto general "usuario"; no se instancia directamente, solo `Cliente` o `Administrador`. |
| **Interfaces** | `dao.Repositorio<T>` (implementada por `VideojuegoDAO` y `CompraDAO`), `dao.UsuarioRepositorio` (implementada por `UsuarioDAO`) y `descuento.EstrategiaDescuento` (implementada por `SinDescuento`, `DescuentoPorCantidad`, `DescuentoPorMonto`) | Los servicios las usan como tipo, sin conocer la clase concreta. |
| **Manejo de errores** | `excepciones.GameStoreException` (extends `Exception`) y sus hijas `DatosInvalidosException`, `StockInsuficienteException`, `CredencialesInvalidasException` | Cada ventana captura la excepción con `try/catch` y muestra un mensaje claro en un cuadro de diálogo; el programa nunca se cierra por una entrada inválida. |

### Persistencia

| Concepto | Dónde está | Cómo se usa |
|---|---|---|
| **Base de datos local (SQLite + JDBC)** | `bd.ConexionBD`, `dao.UsuarioDAO`, `dao.VideojuegoDAO`, `dao.CompraDAO`, `schema.sql` | Usuarios, catálogo, compras y detalles se guardan en `gamestore.db` y siguen ahí al cerrar el programa. `CompraDAO.guardar()` registra la compra y descuenta el stock en una sola transacción. |

### Interfaz

| Concepto | Dónde está | Cómo se usa |
|---|---|---|
| **Interfaz gráfica (Swing)** | `vista.LoginFrame`, `vista.ClienteFrame`, `vista.AdminFrame` | Inicio de sesión/registro; el cliente ve catálogo, carrito, compra y sus pedidos; el administrador hace CRUD de videojuegos y gestiona pedidos. |

### Principios SOLID

| Principio | Dónde está | Cómo se cumple |
|---|---|---|
| **S** — Responsabilidad única | `vista` (pantallas), `servicio` (reglas), `dao` (base de datos), `modelo` (datos) | Cada clase hace una sola cosa: p. ej. `AuthService` solo autentica y `CompraDAO` solo guarda compras. |
| **O** — Abierto/Cerrado | `servicio.CompraService` + `descuento.*` | Para agregar un descuento nuevo solo se crea otra clase que implemente `EstrategiaDescuento` y se agrega a la lista en `TiendaFacade`; `CompraService` no se modifica. |
| **L** — Sustitución de Liskov | `Cliente`/`Administrador` por `Usuario`; las 3 estrategias por `EstrategiaDescuento` | Se pueden usar en lugar del tipo padre/interfaz sin romper el programa (ej. `AuthService.iniciarSesion` devuelve `Usuario`). |
| **I** — Segregación de interfaces | `dao.Repositorio`, `dao.UsuarioRepositorio`, `dao.CompraRepositorio`, `EstrategiaDescuento` | Interfaces pequeñas: `EstrategiaDescuento` tiene 2 métodos; `UsuarioRepositorio` solo tiene `guardar` y `buscarPorNombreUsuario`, que es lo único que usa `AuthService`, y no hereda los métodos generales de `Repositorio`. |
| **D** — Inversión de dependencias | Constructores de `AuthService`, `CatalogoService`, `CompraService` | Reciben interfaces (`UsuarioRepositorio`, `Repositorio<Videojuego>`, `CompraRepositorio`, `List<EstrategiaDescuento>`), no clases concretas. `TiendaFacade` es quien las conecta. |

### Patrones de diseño

| Tipo | Patrón | Dónde está | Cómo se usa |
|---|---|---|---|
| **Creacional** | **Singleton** | `bd.ConexionBD` | Solo existe una instancia que maneja la conexión a SQLite; todos los DAO la obtienen con `ConexionBD.getInstancia()`. |
| **Estructural** | **Facade** | `fachada.TiendaFacade` | Las ventanas solo hablan con `TiendaFacade`; esta esconde servicios, DAO y base de datos detrás de métodos simples (`comprar`, `iniciarSesion`, `listarVideojuegos`…). |
| **Comportamiento** | **Strategy** | `descuento.EstrategiaDescuento` y sus 3 implementaciones; usado en `servicio.CompraService` | Al comprar, el servicio elige la estrategia que más descuento da (10 % por 3+ juegos o 5 % desde $150) y el resultado se ve en el carrito y en el total. |

## Flujo de uso

1. **Cliente:** Registrarse → Iniciar sesión → *Catálogo* (buscar y agregar) → *Mi carrito* (ver descuento, escribir dirección, confirmar) → *Mis compras* (ver estado: PENDIENTE / ENVIADO).
2. **Administrador:** Iniciar sesión → *Catálogo* (agregar, modificar, eliminar, cambiar stock) → *Pedidos* (ver detalle y dirección, marcar como enviado).
