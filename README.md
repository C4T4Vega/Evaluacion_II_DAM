# LexLector — Aplicación móvil para el estudio de leyes chilenas

LexLector es una aplicación móvil para Android desarrollada con **Kotlin** y **Jetpack Compose** (desafío *Código CH*). Su objetivo es ayudar a estudiantes de Derecho y abogados a consultar leyes vigentes, revisar sus cambios históricos por año y organizar sus apuntes de estudio en un solo lugar.

Este README describe lo que está implementado actualmente en el código fuente. Está pensado como guía rápida para entender cómo funciona el proyecto y cómo ejecutarlo.

## Índice

* [Qué resuelve](#qué-resuelve)
* [Arquitectura y cómo viajan los datos](#arquitectura-y-cómo-viajan-los-datos)
* [Modelo de datos](#modelo-de-datos)
* [Reglas y validaciones importantes](#reglas-y-validaciones-importantes)
* [Modo oscuro](#modo-oscuro)
* [Pantallas de la aplicación](#pantallas-de-la-aplicación)
* [Ejecución local](#ejecución-local)
* [Estructura del proyecto](#estructura-del-proyecto)
* [Autores](#autores)

---

## Qué resuelve

La aplicación permite:

* Ver una pantalla de carga inicial y una pantalla de bienvenida.
* Registrar nuevos usuarios e iniciar sesión con cuentas de prueba guardadas en memoria.
* Validar que los datos ingresados en los formularios sean correctos antes de continuar.
* Ver el perfil del usuario conectado, activar o desactivar el modo oscuro y cerrar sesión con un mensaje de confirmación.
* Explorar un catálogo de leyes chilenas separado en documentos actualizados recientemente y listado completo.
* Abrir la ficha de detalle de una ley para ver su autor, año de publicación, año actual y resumen.
* Consultar el historial de modificaciones que ha tenido una ley ordenado por año.

---

## Arquitectura y cómo viajan los datos

El proyecto está organizado usando el patrón **MVVM (Model - View - ViewModel)** para separar las pantallas visuales de la lógica y los datos:

```text
Pantalla UI (@Composable)
   ↓ Muestra los datos en pantalla y avisa cuando el usuario toca un botón
ViewModel
   ↓ Guarda el estado de la pantalla y aplica las validaciones
Repository
   ↓ Guarda las listas de prueba en memoria (usuarios y leyes)
Model
   ↓ Define la estructura de cada objeto (data class)
```

Por ejemplo, cuando el usuario inicia sesión:

```text
LoginScreen
   ↓ Envía el correo y contraseña escritos
AuthViewModel
   ↓ Revisa que los campos no estén vacíos y consulta al repositorio
UsuarioRepository
   ↓ Busca si el usuario existe en la lista y devuelve el resultado
MainActivity
   ↓ Si el login es correcto, cambia a la pantalla principal
```

---

## Modelo de datos

| Clase (`model`) | Para qué sirve | Datos que guarda |
| :--- | :--- | :--- |
| `Usuario` | Representa a un usuario de la app. | `id`, `nombre`, `email`, `password` |
| `Ley` | Representa un código o ley disponible en el catálogo. | `id`, `nombre`, `autor`, `anioPublicacion`, `anioActual`, `esReciente`, `descripcion`, `historial` |
| `CambioLey` | Representa una modificación histórica dentro de una ley. | `anio`, `detalle` |
| `Items_bottom_nav` | Representa cada botón del menú inferior. | `icon`, `title`, `ruta` |

### Relación entre leyes e historial

```text
Ley ── contiene una lista de ──> CambioLey (año y detalle del cambio)
```

Cada vez que se selecciona una ley en el catálogo, la pantalla de detalle recibe esa ley y muestra tanto su información general como su lista de cambios por año.

---

## Reglas y validaciones importantes

### Inicio de sesión y registro (`AuthViewModel`)

Al intentar iniciar sesión o crear una cuenta, la app revisa lo siguiente:

1. Que ningún campo obligatorio esté vacío.
2. Que el correo electrónico contenga un `@`.
3. En el registro, que la contraseña y la confirmación de contraseña sean iguales.
4. Que el correo no esté registrado previamente en `UsuarioRepository`.

Si alguna validación falla, se muestra un mensaje de error en pantalla. Si todo está correcto, se guarda al usuario como sesión activa.

### Usuarios de prueba iniciales

Al abrir la aplicación, `UsuarioRepository` ya viene cargado con usuarios de prueba para poder entrar directamente sin tener que registrarse desde cero:

* **Correo:** `vicente@duocuc.cl` | **Contraseña:** `123456`
* **Correo:** `admin@lexlector.cl` | **Contraseña:** `admin123`

### Cierre de sesión

Al presionar el botón de cerrar sesión en el perfil, no se cierra la cuenta de inmediato: primero se abre una ventana de confirmación (`AlertDialog`) para evitar que el usuario salga por error.

### Selección de una ley en el catálogo (`LeyViewModel`)

* La sección **"Actualizados recientemente"** filtra automáticamente las leyes que tienen `esReciente = true`.
* Al tocar cualquier ley, `LeyViewModel` la guarda en la variable `leySeleccionada` y navega a la pantalla de detalle para mostrar sus datos.

---

## Modo oscuro

El estado del modo oscuro se maneja con una variable booleana (`modoOscuro`) dentro de `AuthViewModel`.

En `MainActivity`, toda la aplicación está envuelta dentro del tema `LexLectorTheme(darkTheme = authViewModel.modoOscuro)`. Cuando el usuario activa el interruptor (`Switch`) en su pantalla de perfil, el valor cambia y toda la aplicación actualiza sus colores de inmediato.

---

## Pantallas de la aplicación

### Flujo de entrada (`MainActivity`)

| Pantalla | Descripción |
| :--- | :--- |
| `SplashScreen` | Pantalla de carga inicial con el logo de la aplicación. |
| `BienvenidaScreen` | Pantalla de presentación con botones para ir a Iniciar Sesión o Registrarse. |
| `LoginScreen` | Formulario para entrar con correo y contraseña. |
| `RegistroScreen` | Formulario para crear una cuenta nueva en memoria. |

### Navegación principal con barra inferior (`Navegacion.kt`)

Una vez que el usuario inicia sesión, entra a `PrincipalScreen`, que tiene el menú inferior (`NavInferior`) para moverse entre las secciones:

| Ruta (`NavScreen`) | Pantalla | Descripción |
| :--- | :--- | :--- |
| `HomeScreen` | `HomeScreen.kt` | Pantalla de inicio con buscador, avisos y leyes vistas recientemente. |
| `CatalogoScreen` | `CatalogoScreen.kt` | Catálogo con leyes actualizadas recientemente y lista completa de documentos. |
| `DetalleLeyScreen` | `DetalleLeyScreen.kt` | Ficha con los datos de la ley seleccionada, resumen e historial de cambios por año. |
| `PerfilScreen` | `PerfilScreen.kt` | Muestra el nombre y correo del usuario, switch de modo oscuro y botón para cerrar sesión. |

---

## Ejecución local

### Requisitos

* **Android Studio** (versión Koala, Ladybug o superior).
* **JDK 17** (incluido con Android Studio).
* Emulador de Android configurado o dispositivo físico con **Android 8.0 (API 26)** o superior.

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
├── model/                  # Clases de datos (Usuario, Ley, CambioLey, Items_bottom_nav)
├── repository/             # Datos de prueba en memoria (UsuarioRepository, LeyRepository)
├── viewmodel/              # Lógica de las pantallas (AuthViewModel, LeyViewModel)
├── ui/                     # Parte visual de la aplicación
│   ├── components/         # Componentes reutilizables (NavInferior)
│   ├── navigation/         # Rutas y controlador de navegación (Navegacion, NavScreen)
│   ├── theme/              # Colores, fuentes y configuración del tema claro/oscuro
│   ├── BienvenidaScreen.kt
│   ├── CatalogoScreen.kt
│   ├── DetalleLeyScreen.kt
│   ├── HomeScreen.kt
│   ├── LoginScreen.kt
│   ├── PerfilScreen.kt
│   ├── PrincipalScreen.kt
│   └── RegistroScreen.kt
└── MainActivity.kt         # Actividad principal que inicia la app
```

---

## Autores

* Vicente Curitol
* Catalina Vega
