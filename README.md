# LexLector — Aplicación móvil para el estudio de leyes chilenas

LexLector es una aplicación móvil para Android desarrollada con **Kotlin** y **Jetpack Compose** (desafío *Código CH*). Su objetivo es ayudar a estudiantes de Derecho y abogados a consultar leyes vigentes, leer sus artículos paso a paso, revisar sus cambios históricos por año y organizar apuntes con mapas conceptuales en un solo lugar.

Técnicamente, la app consume una **API REST en Xano** con Retrofit + Gson, persiste datos con **Room (SQLite)** y `SharedPreferences`, y utiliza dos **recursos nativos del dispositivo**: la **cámara** y las **notificaciones**. Además incluye **pruebas unitarias** con JUnit 4, corrutinas y MockWebServer.

Este README describe el comportamiento implementado actualmente en el código fuente. Está pensado como guía rápida para entender cómo funciona el proyecto, cómo ejecutarlo y cómo verificarlo.

## Índice

* [Qué resuelve](#qué-resuelve)
* [Arquitectura y cómo viajan los datos](#arquitectura-y-cómo-viajan-los-datos)
* [Modelo de datos](#modelo-de-datos)
* [API REST (Xano) y persistencia local](#api-rest-xano-y-persistencia-local)
* [Reglas y funciones importantes](#reglas-y-funciones-importantes)
* [Recursos nativos del dispositivo y modo oscuro](#recursos-nativos-del-dispositivo-y-modo-oscuro)
* [Pantallas de la aplicación](#pantallas-de-la-aplicación)
* [Dependencias y configuración](#dependencias-y-configuración)
* [Ejecución local](#ejecución-local)
* [Estructura del proyecto](#estructura-del-proyecto)
* [Pruebas unitarias automatizadas](#pruebas-unitarias-automatizadas)
* [Pruebas del recorrido completo](#pruebas-del-recorrido-completo)
* [Autores](#autores)

---

## Qué resuelve

La aplicación permite:

* Ver una pantalla de carga inicial y una pantalla de bienvenida.
* Registrar nuevos usuarios e iniciar sesión con cuentas de prueba.
* Mantener la sesión iniciada en el teléfono usando `SharedPreferences`, para que al cerrar y volver a abrir la app no pida contraseña de nuevo.
* Buscar leyes por nombre, autor o descripción desde la pantalla de inicio y ver el historial de leyes abiertas recientemente.
* Explorar el catálogo de leyes chilenas separado en documentos actualizados recientemente y listado completo.
* Abrir la ficha de detalle de una ley para ver su autor, año de publicación, año actual, descripción e historial de modificaciones ordenado por año.
* Leer los artículos de cada ley paso a paso en un lector con barra de progreso y botones de paginación (*Anterior* y *Siguiente*).
* Crear, ver, editar y eliminar apuntes de estudio asociados a una ley, incluyendo un generador de **mapas conceptuales** que se dibuja en pantalla mientras escribes.
* Ver el perfil del usuario conectado, tomarse una foto de perfil usando la **cámara nativa del dispositivo**, activar o desactivar el modo oscuro y cerrar sesión con un mensaje de confirmación.
* Sincronizar usuarios y apuntes con una **API REST (Xano)** y conservarlos en **Room SQLite** para funcionar también sin internet.
* Recibir una **notificación nativa** de "Sesión iniciada" al hacer login, previa solicitud del permiso `POST_NOTIFICATIONS` (solo al iniciar sesión, no al abrir la app).

---

## Arquitectura y cómo viajan los datos

El proyecto está organizado usando el patrón **MVVM (Model - View - ViewModel)** para separar las pantallas visuales de la lógica y los datos. Los repositorios son la puerta de entrada a los datos locales y remotos:

```text
Pantalla UI (@Composable)
   ↓ Muestra los datos en pantalla y avisa cuando el usuario toca un botón
ViewModel
   ↓ Guarda el estado de la pantalla, filtra listas y aplica las validaciones
Repository
   ↓ Coordina tres fuentes de datos
   ├── API REST (Xano)  → Retrofit + Gson  → modelos (data class)
   ├── Room (SQLite)    → AppDatabase / UsuarioDao / ApunteDao
   └── SharedPreferences → sesión y preferencias (tema oscuro)
Model
   ↓ Define la estructura de cada objeto (data class)
```

Por ejemplo, cuando el usuario inicia sesión o vuelve a abrir la app:

```text
MainActivity
   ↓ Llama a UsuarioRepository.iniciar(context) al arrancar
UsuarioRepository
   ↓ Lee la BD Room y revisa en SharedPreferences si hay un correo guardado
   ↓ Si no hay datos locales, siembra las cuentas de prueba y sincroniza con GET /usuarios
MainActivity
   ↓ Si ya había sesión activa, salta la bienvenida y abre directo PrincipalScreen
   ↓ Si el usuario viene del LoginScreen, pasa por LoginConNotificacion antes del home
```

---

## Modelo de datos

| Clase (`model`) | Para qué sirve | Datos que guarda |
| :--- | :--- | :--- |
| `Usuario` | Representa a un usuario registrado en la app. | `id`, `nombre`, `apellido`, `correo`, `contrasena`, `rol` |
| `Ley` | Representa un código o ley disponible en el catálogo. | `id`, `nombre`, `autor`, `anioPublicacion`, `anioActual`, `esReciente`, `descripcion`, `historial` |
| `CambioLey` | Representa una modificación histórica dentro de una ley. | `anio`, `detalle` |
| `Apunte` | Representa una nota de estudio con su mapa conceptual. | `id`, `titulo`, `contenido`, `leyNombre`, `conceptoPrincipal`, `subconcepto1`, `subconcepto2`, `usuarioCorreo` |
| `Items_bottom_nav` | Representa cada botón del menú inferior. | `icon`, `title`, `ruta` |

### Relación funcional

```text
Usuario ── crea y administra ──> Apunte (asociado a una Ley y con Mapa Conceptual)
Ley     ── contiene lista de ──> CambioLey (año y detalle de cada reforma)
```

---

## API REST (Xano) y persistencia local

### Cliente HTTP (`remote/`)

`ApiService.kt` declara los 9 endpoints de Xano y `RetrofitInstance.kt` configura Retrofit con `GsonConverterFactory`:

```kotlin
private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:C8WEkoLD/"

val api: ApiService by lazy { crearApi(BASE_URL) }

fun crearApi(baseUrl: String): ApiService = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ApiService::class.java)
```

`crearApi(baseUrl)` está separado a propósito para que las pruebas unitarias puedan apuntar Retrofit a un `MockWebServer` sin modificar la URL real.

| Método | Endpoint | Función | Usado por |
| :--- | :--- | :--- | :--- |
| `GET` | `leyes` | `obtenerLeyes()` | `LeyRepository` → `LeyViewModel` |
| `GET` | `leyes/{id}` | `obtenerLeyPorId(id)` | `LeyRepository` |
| `POST` | `leyes` | `crearLey(ley)` | `LeyRepository` |
| `GET` | `apuntes` | `obtenerApuntes()` | `ApunteRepository` |
| `POST` | `apuntes` | `crearApunte(apunte)` | `ApunteRepository` |
| `PATCH` | `apuntes/{id}` | `actualizarApunte(id, apunte)` | `ApunteRepository` |
| `DELETE` | `apuntes/{id}` | `eliminarApunte(id)` | `ApunteRepository` |
| `GET` | `usuarios` | `obtenerUsuarios()` | `UsuarioRepository` |
| `POST` | `usuarios` | `registrarUsuario(usuario)` | `UsuarioRepository` |

Las llamadas se hacen con corrutinas (`suspend fun`) y se ejecutan en `Dispatchers.IO`. Cada sincronización va envuelta en `try/catch`: si falla la red, la app sigue funcionando con los datos locales.

### Persistencia (`model/AppDatabase.kt` y repositorios)

* **Room SQLite:** base de datos `lexlector_db` (versión 1) con las entidades `Usuario` y `Apunte`.
  * `UsuarioDao`: `getAll()` e `insert()` con `OnConflictStrategy.REPLACE`.
  * `ApunteDao`: `getAll()`, `insert()`, `update()` y `deleteById(id)`.
  * `AppDatabase.getDatabase(context)` devuelve una única instancia (`singleton` con `@Volatile` + `synchronized`).
* **Lista local de respaldo:** `LeyRepository.listaLeyes` contiene 3 leyes precargadas que se muestran mientras carga la API o cuando no hay internet.
* **`SharedPreferences("lexlector_sesion")`:**
  * `correo_sesion`: recuerda al usuario logueado entre aperturas de la app.
  * `modo_oscuro`: guarda la preferencia de tema.

---

## Reglas y funciones importantes

### Inicio de sesión, registro y persistencia (`AuthViewModel` y `UsuarioRepository`)

Al iniciar sesión o crear una cuenta, la app revisa lo siguiente:

1. Que ningún campo obligatorio esté vacío.
2. Que el correo no esté registrado previamente al crear una cuenta nueva.
3. Si el login es correcto, guarda el correo en `SharedPreferences` (`"correo_sesion"`) para recordar al usuario aunque cierre la aplicación.
4. Al cerrar sesión desde el perfil, primero muestra una ventana de confirmación (`AlertDialog`) y, al confirmar, borra el correo guardado en el teléfono.

### Usuarios de prueba iniciales

Al abrir la aplicación, `UsuarioRepository` ya viene cargado con dos cuentas de prueba para entrar directamente:

* **Correo:** `estudiante@lexlector.cl` | **Contraseña:** `123456`
* **Correo:** `abogada@lexlector.cl` | **Contraseña:** `123456`

### Catálogo, buscador y lector de leyes (`LeyViewModel` y `LectorScreen`)

* **Actualizados recientemente:** Filtra automáticamente las leyes que tienen `esReciente = true`.
* **Vistos recientemente:** Cada vez que el usuario abre una ley, se agrega al inicio de la fila de leyes recientes en el Inicio sin repetirse.
* **Buscador:** Permite buscar coincidencias por nombre, autor o descripción sin distinguir mayúsculas de minúsculas.
* **Lector paginado:** Calcula el porcentaje de avance según el artículo actual (`indiceArticulo`) y desactiva los botones *Anterior* o *Siguiente* cuando se llega al primer o último artículo.

### Apuntes y mapas conceptuales (`ApunteRepository` y `CrearNotaScreen`)

* Para guardar un apunte es obligatorio ingresar al menos el título y el contenido.
* Si el estudiante completa los campos de **Concepto Principal** y **Subconceptos**, la tarjeta inferior dibuja el mapa conceptual conectado con flechas en tiempo real.
* Los apuntes creados guardan el correo del usuario conectado (`usuarioCorreo`) para mostrarse en su pestaña personal de **Apuntes**, y también permiten editarse o eliminarse.

---

## Recursos nativos del dispositivo y modo oscuro

### 1. Cámara del dispositivo (`PerfilScreen`)

* Se declara el permiso `android.permission.CAMERA` en `AndroidManifest.xml`.
* En la pantalla de perfil se utiliza `rememberLauncherForActivityResult` con `ActivityResultContracts.TakePicturePreview()`.
* Si el permiso no está concedido, primero se pide con `ActivityResultContracts.RequestPermission()`; al concederlo se abre la cámara.
* Cuando el usuario toma la fotografía, el `Bitmap` recibido se guarda en `AuthViewModel` y reemplaza el avatar circular del perfil.

### 2. Notificaciones del dispositivo (`ui/notificaciones/`)

* Permiso declarado: `android.permission.POST_NOTIFICATIONS` en `AndroidManifest.xml`.
* `NotificacionLogin.kt` crea el canal `lexlector_inicio_sesion` (importancia `DEFAULT`, para que el aviso quede en la **bandeja** del teléfono), verifica el permiso con `ContextCompat.checkSelfPermission` y envía la notificación con `NotificationManagerCompat.notify`.
  * Título: **"Sesión iniciada"**.
  * Texto: **"Hola `<nombre>`, iniciaste sesión en LexLector."**
  * El `PendingIntent` abre la app al tocar la notificación.
  * El small icon es un vector drawable `res/drawable/ic_notificaciones_activas.xml`, equivalente al icono `NotificationsActive` de Material Icons. En Android el small icon debe ser una silueta blanca; el sistema lo tiñe y `setColor` usa el azul de la app para el badge.
* `PermisoNotificacionesScreen.kt` lanza directamente el diálogo de permiso del sistema justo después del login y **antes de entrar al home**. No muestra un diálogo propio.
* `LoginConNotificacion.kt` envuelve al `LoginScreen` existente y activa ese flujo **solo cuando `iniciarSesion()` devuelve `true`**. `MainActivity` usa este wrapper únicamente en la rama `"login"`.

```text
LoginScreen → (login correcto) → LoginConNotificacion
   → PermisoNotificacionesScreen (pide POST_NOTIFICATIONS)
   → NotificacionLogin.mostrar("Sesión iniciada")
   → PrincipalScreen (home)
```

**Regla importante:** abrir la app con una sesión guardada, navegar al home o cambiar de pestaña **no** vuelve a pedir permiso ni a notificar. Si el permiso ya estaba concedido, tampoco se vuelve a preguntar, pero se envía la notificación en cada inicio de sesión.

### Modo oscuro

* El estado del tema se maneja con la variable `modoOscuro` dentro de `AuthViewModel`.
* En `MainActivity`, toda la aplicación está envuelta en `LexLectorTheme(darkTheme = authViewModel.modoOscuro)`. Al mover el interruptor (`Switch`) en el perfil, toda la app cambia sus colores al instante.

---

## Pantallas de la aplicación

### Flujo de entrada (`MainActivity`)

| Pantalla | Descripción |
| :--- | :--- |
| `SplashScreen` | Pantalla de carga inicial con el logo de la aplicación por 2 segundos. |
| `BienvenidaScreen` | Presentación inicial con botones para ir a Iniciar Sesión o Registrarse. |
| `LoginScreen` | Formulario para entrar con correo y contraseña. |
| `RegistroScreen` | Formulario para registrar un usuario nuevo. |

> Al iniciar sesión correctamente, `MainActivity` muestra `LoginConNotificacion`, que gestiona el permiso y la notificación antes de mostrar `PrincipalScreen`. El resto del flujo de entrada no se ve afectado.

### Navegación principal con barra inferior (`Navegacion.kt`)

El menú inferior (`NavInferior`) tiene 4 secciones principales (**Inicio**, **Leyes**, **Apuntes** y **Perfil**) y conecta con las pantallas internas:

| Ruta (`NavScreen`) | Pantalla | Descripción |
| :--- | :--- | :--- |
| `HomeScreen` | `HomeScreen.kt` | Inicio con buscador desplegable, avisos, leyes vistas recientemente y últimos apuntes. |
| `CatalogoScreen` | `CatalogoScreen.kt` | Catálogo con carrusel de leyes actualizadas recientemente y cuadrícula completa. |
| `DetalleLeyScreen` | `DetalleLeyScreen.kt` | Ficha técnica de la ley, botones para ir a Leer o Ver Apuntes e historial de cambios por año. |
| `LectorScreen` | `LectorScreen.kt` | Lector de artículos paso a paso con barra de progreso y botones Anterior y Siguiente. |
| `ApuntesScreen` | `ApuntesScreen.kt` | Pestaña del menú inferior que muestra en cuadrícula todos los apuntes del usuario conectado. |
| `DetalleApunteScreen` | `DetalleApunteScreen.kt` | Muestra el contenido completo de un apunte, su mapa conceptual y el botón para editarlo. |
| `ListaApuntesScreen` | `ListaApuntesScreen.kt` | Lista de apuntes filtrada por la ley seleccionada, con botón `+` para crear y basurero para borrar. |
| `CrearNotaScreen` | `CrearNotaScreen.kt` | Formulario para crear o editar un apunte con vista previa del mapa conceptual en vivo. |
| `PerfilScreen` | `PerfilScreen.kt` | Datos del usuario, botón para tomar foto con la cámara, switch de modo oscuro y cierre de sesión. |

---

## Dependencias y configuración

| Concepto | Versión / valor |
| :--- | :--- |
| Lenguaje | Kotlin 2.2.10 |
| Android Gradle Plugin | 9.2.1 |
| `compileSdk` / `minSdk` / `targetSdk` | 37 / 35 / 37 |
| Jetpack Compose | BOM 2026.02.01 + Material 3 |
| Room | 2.7.2 (con KSP 2.2.10-2.0.2) |
| Retrofit + Gson | 2.11.0 |
| Corrutinas | 1.9.0 |
| Iconos | `material-icons-extended-android` 1.7.8 |
| Navegación | `hilt-navigation-compose` 1.4.0 |
| Splash screen | `core-splashscreen` 1.2.0 |
| Pruebas | JUnit 4.13.2, `kotlinx-coroutines-test`, MockWebServer 4.12.0 |

Permisos declarados en `AndroidManifest.xml`: `CAMERA`, `INTERNET`, `POST_NOTIFICATIONS` y `uses-feature android.hardware.camera` (no obligatorio).

---

## Ejecución local

### Requisitos

* **Android Studio**
* **JDK 17** (incluido con Android Studio; el proyecto compila con target JVM 11).
* Emulador de Android o dispositivo físico con **API 35 o superior** (`minSdk = 35`).
* Conexión a internet para la sincronización con Xano (la app funciona con datos locales si no la hay).

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/C4T4Vega/Evaluacion_II_DAM.git
   ```
2. Abrir **Android Studio** y seleccionar **Open**, eligiendo la carpeta del proyecto.
3. Esperar a que termine de cargar y sincronizar **Gradle**.
4. Seleccionar un emulador o celular conectado arriba y presionar el botón **Run 'app'** (icono de Play verde).

---

## Estructura del proyecto

```text
app/src/main/java/com/dinamite/lexlector/
├── model/                  # Data classes y Room (AppDatabase, UsuarioDao, ApunteDao, entidades)
├── remote/                 # ApiService (9 endpoints) y RetrofitInstance (Retrofit + Gson)
├── repository/             # UsuarioRepository, LeyRepository, ApunteRepository
├── viewmodel/              # Lógica y estado de las pantallas (AuthViewModel, LeyViewModel)
├── ui/                     # Pantallas y componentes visuales en Jetpack Compose
│   ├── components/         # NavInferior, TarjetaLibro, TarjetaLibroApunte
│   ├── navigation/         # Navegacion, NavScreen, NavigationUtils
│   ├── notificaciones/     # NotificacionLogin, PermisoNotificacionesScreen, LoginConNotificacion
│   ├── theme/              # Colores, fuentes y configuración del tema claro/oscuro
│   ├── ApuntesScreen.kt
│   ├── BienvenidaScreen.kt
│   ├── CatalogoScreen.kt
│   ├── CrearNotaScreen.kt
│   ├── DetalleApunteScreen.kt
│   ├── DetalleLeyScreen.kt
│   ├── HomeScreen.kt
│   ├── LectorScreen.kt
│   ├── ListaApuntesScreen.kt
│   ├── LoginScreen.kt
│   ├── PerfilScreen.kt
│   ├── PrincipalScreen.kt
│   └── RegistroScreen.kt
└── MainActivity.kt         # Splash, tema, sesión y enrutamiento inicial

app/src/main/res/
├── drawable/ic_notificaciones_activas.xml   # Small icon blanco de la notificación
├── font/, mipmap/, values/, xml/            # Tipografía, iconos de launcher, colores y temas

app/src/test/java/com/dinamite/lexlector/
├── remote/ApiServiceTest.kt          # Prueba de la API con MockWebServer
├── repository/LeyRepositoryTest.kt   # Prueba del repositorio con MockWebServer
└── viewmodel/LeyViewModelTest.kt     # Pruebas del ViewModel con repositorio falso
```

---

## Pruebas unitarias automatizadas

Se ejecutan en la JVM (no necesitan emulador) y viven en `app/src/test`. Hay **5 pruebas**:

| Clase | Cuántas | Qué verifica |
| :--- | :--- | :--- |
| `LeyViewModelTest` | 3 | Carga y filtro de leyes recientes, respaldo local cuando falla la red y buscador. Usa un `FakeRepository` que implementa `LeyesDataSource`. |
| `ApiServiceTest` | 1 | Retrofit + Gson convierten el JSON de un `MockWebServer` en objetos `Ley` y se llama `GET /leyes`. |
| `LeyRepositoryTest` | 1 | `LeyRepository.obtenerLeyesApi()` delega correctamente en la API y devuelve las leyes del servidor falso. |

### Cómo ejecutarlas

```powershell
# Todas las pruebas unitarias de debug
.\gradlew.bat :app:testDebugUnitTest

# Una sola clase
.\gradlew.bat :app:testDebugUnitTest --tests "com.dinamite.lexlector.remote.ApiServiceTest"
```

En Android Studio: click derecho sobre la clase de test o sobre `app/src/test` → **Run**, o bien panel **Gradle → app → Tasks → verification → testDebugUnitTest**.

Resultado esperado:

```text
ApiServiceTest > obtenerLeyes convierte el JSON del servidor en objetos Ley PASSED
LeyRepositoryTest > obtenerLeyesApi del repositorio devuelve lo que responde la API PASSED
LeyViewModelTest > (3 pruebas) PASSED
BUILD SUCCESSFUL
```

El reporte HTML queda en `app/build/reports/tests/testDebugUnitTest/index.html` y los resultados XML en `app/build/test-results/testDebugUnitTest/`.

**Nota técnica:** como `RetrofitInstance.api` es un `val by lazy` con URL fija, `LeyRepositoryTest` reemplaza temporalmente su delegate en memoria para apuntarlo al `MockWebServer` y lo restaura en `@After`. Así se prueba el repositorio real sin modificar `RetrofitInstance.kt`.

---

## Pruebas del recorrido completo

Para comprobar la integración de todos los módulos del proyecto, se verificó el siguiente recorrido de principio a fin:

1. **Inicio de sesión y persistencia:** Ingresar con `estudiante@lexlector.cl` y clave `123456`. Cerrar la app por completo y volver a abrirla para comprobar que entra directo a la pantalla de Inicio sin pedir login nuevamente.
2. **Búsqueda y catálogo:** Buscar una norma en la lupa de `HomeScreen` o seleccionarla desde la pestaña `Leyes` y verificar que aparece en la fila de *Vistos recientemente*.
3. **Detalle y lector:** Abrir el *Código Civil*, revisar su historial de reformas por año y presionar **Leer** para avanzar y retroceder entre sus artículos viendo cómo sube la barra de progreso.
4. **Creación y edición de apuntes:** Volver al detalle de la ley, presionar **Ver Apuntes**, tocar el botón `+`, redactar una nota con su mapa conceptual y guardarla. Verificar que aparece en la lista y en la pestaña general **Apuntes**, desde donde también se puede editar o eliminar.
5. **Perfil, cámara y cierre de sesión:** Ir a la pestaña **Perfil**, tomar una foto con la cámara nativa comprobando que cambia el avatar, activar el **Modo Oscuro** y finalmente cerrar sesión mediante el cuadro de confirmación.
6. **Notificación de inicio de sesión:** volver a ingresar con una cuenta de prueba. Debe aparecer el permiso del sistema `POST_NOTIFICATIONS`; al aceptarlo se envía a la bandeja **"Sesión iniciada / Hola `<nombre>`"** y recién después se abre el home. Cerrar y volver a abrir la app con la sesión guardada **no** debe pedir permiso ni notificar.

---

## Autores

* Vicente Curitol
* Catalina Vega
