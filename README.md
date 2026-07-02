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
├── docs/PLAY_STORE.md     # Checklist publicación Play Store
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
└── puzzles/               # 10 módulos de puzzle (todos jugables)
```

## Estado actual — Fase 7

- **Progresión completa (Fase 6):** 10 tiers, balanceo vía `progression_config.json`, mapa visual en The Vault, rotación de cola al subir tier
- **Ranking local (Fase 3b):** publicar alias en Game Over, top 100 en Room
- **Pulido (Fase 7):** tutorial primera vez, toggles sonido/vibración, estadísticas locales, guía Play Store
- 10 puzzles jugables, monetización y core loop (fases anteriores)

Consulta el [plan completo](docs/PLAN.md) y la [guía Play Store](docs/PLAY_STORE.md).

## Licencia

Proyecto privado — todos los derechos reservados.
