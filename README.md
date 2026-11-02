# 🚜 MonitoreoSilosApp (SiloControl)

Aplicación móvil Android desarrollada en Kotlin para el monitoreo, gestión y control en tiempo real de niveles de alimento en silos agrícolas. Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** en Duoc UC.

---

## 👥 Integrantes del Equipo
- **Samuel Berrios** (@samu189)
- **Benjamín Quintanilla** (@Benjamin-q10)

---

## 📝 Descripción del Proyecto
`MonitoreoSilosApp` es una solución móvil diseñada para optimizar el control de almacenamiento agrícola. Permite a los administradores de granjas registrar nuevos silos, monitorear los niveles de capacidad promedio, recibir alertas de stock crítico y almacenar la información de manera segura y persistente en el dispositivo.

---

## 🚀 Funcionalidades Implementadas

- 📊 **Dashboard Principal (`HomeScreen`):** Visualización modular de resumen de granjas, silos registrados, contador de estados de alerta/críticos e indicador circular de capacidad promedio.
- 📝 **Formulario Validador (`SiloFormScreen`):** Registro de silos con retroalimentación visual en tiempo real, íconos explicativos y validaciones desasociadas de la UI (`SiloValidator`).
- 💾 **Persistencia Local (Room DB):** Gestión de base de datos SQLite con almacenamiento reactivo (`Flow`) y operaciones en segundo plano mediante Corrutinas Kotlin (`SiloDao`).
- 🧭 **Navegación Fluida:** Control de flujo de pantallas mediante `NavHost`, `NavController` y gestión de retorno con `popBackStack()`.
- 📷 **Acceso a Recursos Nativos:** Integración segura y presentación visual personalizada de imágenes (Galería/Cámara) y permisos del sistema.
- 🎨 **Interfaz Moderna:** Diseñada completamente con Jetpack Compose y componentes Material Design 3.

---

## 🛠️ Arquitectura y Tecnologías
- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose
- **Patrón de Arquitectura:** MVVM (Model - View - ViewModel) + Clean Architecture
- **Base de Datos Local:** Room DB / SQLite
- **Gestión de Estado:** `StateFlow` / `collectAsState()`
- **Control de Versiones:** Git & GitHub

---

## 💻 Pasos para Ejecutar el Proyecto

### Requisitos previos:
- Android Studio Ladybug (o versión superior recomendada).
- SDK de Android 26 (Android 8.0) o superior.
- Emulador Android o dispositivo físico con depuración USB activada.

### Instrucciones de ejecución:
1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/samu189/MonitoreoSilosApp.git](https://github.com/samu189/MonitoreoSilosApp.git)
