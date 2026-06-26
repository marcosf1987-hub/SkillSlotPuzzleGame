# SkillSlot

Juego Android freemium que combina **tragamonedas** y **puzzles**. Kotlin + Jetpack Compose, arquitectura multi-módulo con MVVM, Hilt, Room y DataStore.

## Requisitos

- [Android Studio](https://developer.android.com/studio) (Ladybug o superior recomendado)
- JDK 17 (incluido con Android Studio)
- Android SDK con API 35

## Clonar y ejecutar

```bash
git clone https://github.com/<tu-usuario>/SkillSlotPuzzleGame.git
cd SkillSlotPuzzleGame
```

1. Abre la carpeta raíz en **Android Studio** (`File → Open`).
2. Espera a que Gradle sincronice (descarga dependencias la primera vez).
3. Conecta un dispositivo o inicia un emulador (API 26+).
4. Ejecuta la configuración **app** (▶ Run).

Desde terminal (con `JAVA_HOME` configurado):

```bash
./gradlew :app:assembleDebug
```

En Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Estructura del repositorio

```
SkillSlotPuzzleGame/
├── docs/PLAN.md           # Plan de arquitectura y roadmap
├── app/                   # Application, navegación, tema, DI
├── core/
│   ├── model/             # GameState, PuzzleType, ProgressionConfig
│   ├── data/              # Room, DataStore, repositorios
│   └── domain/            # Casos de uso
├── feature-slot/          # UI tragamonedas
├── feature-puzzle/        # Shell común de puzzles
├── feature-progression/     # Mapa de progresión
├── feature-leaderboard/     # Ranking y game over
├── puzzle-engine/         # IPuzzle, PuzzleRegistry
└── puzzles/               # 10 módulos de puzzle (stubs en Fase 0)
```

## Estado actual — Fase 2

- **Shell de puzzle**: timer por tier, indicador de vidas, botón pausa
- **Pausa / abandonar**: diálogo con reanudar o perder 1 vida
- **Victoria / derrota**: overlays animados antes de volver a slots
- **Transiciones**: banner al regresar a tragamonedas tras puzzle
- Tragamonedas, sopa de letras y core loop (Fase 1)

Consulta el [plan completo](docs/PLAN.md) — siguiente: **Fase 3** (monetización).

## Licencia

Proyecto privado — todos los derechos reservados.
