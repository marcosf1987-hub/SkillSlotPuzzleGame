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
└── puzzles/               # 10 módulos de puzzle (todos jugables)
```

## Estado actual — Fase 5

- **10 puzzles jugables** — catálogo completo según `PuzzleType.defaultQueue`
- **Nuevos en esta fase:** Memory, Nonogram, Sliding, Conectar puntos, Secuencia luminosa (Simon)
- Cada puzzle escala con el **tier**; generación procedural con `seed`
- Monetización, shell con timer/vidas y core loop (fases anteriores)

| # | Puzzle | Mecánica |
|---|--------|----------|
| 6 | Memory | Voltear parejas de cartas |
| 7 | Nonogram | Rellenar según pistas numéricas |
| 8 | Sliding | Ordenar piezas 3×3 / 4×4 |
| 9 | Conectar | Unir pares de números sin cruzar |
| 10 | Secuencia | Repetir patrón tipo Simon |

Consulta el [plan completo](docs/PLAN.md) — siguiente: **Fase 6** (progresión completa, 10 tiers, balanceo).

## Licencia

Proyecto privado — todos los derechos reservados.
