# LexLector — Aplicación móvil para el estudio de leyes chilenas

LexLector es una aplicación móvil para Android desarrollada con **Kotlin** y **Jetpack Compose** (desafío *Código CH*). Su objetivo es ayudar a estudiantes de Derecho y abogados a consultar leyes vigentes, leer sus artículos paso a paso, revisar sus cambios históricos por año y organizar apuntes con mapas conceptuales en un solo lugar.

Este README describe el comportamiento implementado actualmente en el código fuente. Está pensado como guía rápida para entender cómo funciona el proyecto y cómo ejecutarlo.

## Índice

* [Qué resuelve](#qué-resuelve)
* [Arquitectura y cómo viajan los datos](#arquitectura-y-cómo-viajan-los-datos)
* [Modelo de datos](#modelo-de-datos)
* [Reglas y funciones importantes](#reglas-y-funciones-importantes)
* [Recurso nativo (Cámara) y modo oscuro](#recurso-nativo-cámara-y-modo-oscuro)
* [Pantallas de la aplicación](#pantallas-de-la-aplicación)
* [Ejecución local](#ejecución-local)
* [Estructura del proyecto](#estructura-del-proyecto)
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

---

## Arquitectura y cómo viajan los datos

El proyecto está organizado usando el patrón **MVVM (Model - View - ViewModel)** para separar las pantallas visuales de la lógica y los datos:

```text
Pantalla UI (@Composable)
   ↓ Muestra los datos en pantalla y avisa cuando el usuario toca un botón
ViewModel
   ↓ Guarda el estado de la pantalla, filtra listas y aplica las validaciones
Repository
   ↓ Guarda las listas en memoria (usuarios, leyes y apuntes) y la sesión en el teléfono
Model
   ↓ Define la estructura de cada objeto (data class)
```

Por ejemplo, cuando el usuario inicia sesión o vuelve a abrir la app:

```text
MainActivity
   ↓ Llama a UsuarioRepository.iniciar(context) al arrancar
UsuarioRepository
   ↓ Revisa en SharedPreferences si hay un correo guardado de una sesión anterior
MainActivity
   ↓ Si ya había sesión activa, salta la bienvenida y abre directo PrincipalScreen
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

## Recurso nativo (Cámara) y modo oscuro

### Cámara del dispositivo (`PerfilScreen`)

* Se declara el permiso `android.permission.CAMERA` en `AndroidManifest.xml`.
* En la pantalla de perfil se utiliza `rememberLauncherForActivityResult` con `ActivityResultContracts.TakePicturePreview()`.
* Cuando el usuario toma la fotografía, el `Bitmap` recibido se guarda en `AuthViewModel` y reemplaza el avatar circular del perfil.

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

## Ejecución local

### Requisitos

* **Android Studio** (versión Koala, Ladybug o superior).
* **JDK 11 / 17** (incluido con Android Studio).
* Emulador de Android o dispositivo físico configurado.

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
├── model/                  # Clases de datos (Usuario, Ley, CambioLey, Apunte, Items_bottom_nav)
├── repository/             # Datos en memoria y sesión (UsuarioRepository, LeyRepository, ApunteRepository)
├── viewmodel/              # Lógica y estado de las pantallas (AuthViewModel, LeyViewModel)
├── ui/                     # Pantallas y componentes visuales en Jetpack Compose
│   ├── components/         # Componentes reutilizables (NavInferior, TarjetaLibro, TarjetaLibroApunte)
│   ├── navigation/         # Rutas y controlador de navegación (Navegacion, NavScreen, NavigationUtils)
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
└── MainActivity.kt         # Actividad principal que inicia la app y el tema
```

---

## Pruebas del recorrido completo

Para comprobar la integración de todos los módulos del proyecto, se verificó el siguiente recorrido de principio a fin:

1. **Inicio de sesión y persistencia:** Ingresar con `estudiante@lexlector.cl` y clave `123456`. Cerrar la app por completo y volver a abrirla para comprobar que entra directo a la pantalla de Inicio sin pedir login nuevamente.
2. **Búsqueda y catálogo:** Buscar una norma en la lupa de `HomeScreen` o seleccionarla desde la pestaña `Leyes` y verificar que aparece en la fila de *Vistos recientemente*.
3. **Detalle y lector:** Abrir el *Código Civil*, revisar su historial de reformas por año y presionar **Leer** para avanzar y retroceder entre sus artículos viendo cómo sube la barra de progreso.
4. **Creación y edición de apuntes:** Volver al detalle de la ley, presionar **Ver Apuntes**, tocar el botón `+`, redactar una nota con su mapa conceptual y guardarla. Verificar que aparece en la lista y en la pestaña general **Apuntes**, desde donde también se puede editar o eliminar.
5. **Perfil, cámara y cierre de sesión:** Ir a la pestaña **Perfil**, tomar una foto con la cámara nativa comprobando que cambia el avatar, activar el **Modo Oscuro** y finalmente cerrar sesión mediante el cuadro de confirmación.

---

## Autores

* Vicente Curitol
* Catalina Vega
