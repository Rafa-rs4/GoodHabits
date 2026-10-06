# 📱 GoodHabits

Aplicación Android para **crear, organizar y dar seguimiento a hábitos saludables**. Reúne en un solo lugar hábitos diarios, tareas, meditación, nutrición y ejercicio, y muestra el progreso del usuario.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=flat-square&logo=android&logoColor=white)

## ✨ Funcionalidades

- ✅ **Hábitos:** creación por categorías, registro diario e insignias de logro
- 📝 **Checklist de tareas** con historial
- 🧘 **Meditación** con música de fondo
- 🥗 **Nutrición:** planes de comidas
- 🏋️ **Ejercicio** con mapa y ubicación (osmdroid + GPS)
- 📈 **Progreso:** seguimiento visual del avance
- 🔐 **Inicio de sesión** con Firebase Authentication y Google
- ⚙️ **Ajustes** de usuario

## 🏗️ Arquitectura

Patrón **MVVM** con repositorios:

```
app/src/main/java/com/example/goodhabits/
├── screens/        # Pantallas en Jetpack Compose
├── navigation/     # Navegación entre pantallas
├── viewmodel/      # ViewModels (estado de la UI)
├── dataHabits/     # Entidades, DAOs y repositorio de hábitos (Room)
├── dataTasks/      # Tareas y registros
├── dataMeditation/ # Meditación
├── dataNutrition/  # Planes de nutrición
├── dataMusic/      # Música para meditación
├── dataUser/       # Usuario
├── services/       # Servicios de la app
└── ui/             # Tema y componentes
```

## 🧰 Tecnologías

| Capa | Tecnología |
|:--|:--|
| UI | Jetpack Compose · Material 3 · Navigation Compose · Coil |
| Persistencia local | Room |
| Nube | Firebase Auth · Realtime Database · Cloud Storage |
| Otros | Google Play Services (ubicación), osmdroid (mapas) |

## 🚀 Cómo ejecutarlo

1. Clona el repositorio y ábrelo en **Android Studio**.
2. Agrega tu propio `app/google-services.json` desde la consola de Firebase.
3. Sincroniza Gradle y ejecuta la app en un emulador o dispositivo con Android 7.0 (API 24) o superior.

## 👤 Autor

**Rafael Roncal Saravia**: [LinkedIn](https://www.linkedin.com/in/rafael-roncal-saravia) · [GitHub](https://github.com/Rafa-rs4)
