# SkillSlot — Manual de marca

Referencia extraída del diseño **Lobby** (HTML/Tailwind). Implementación en Compose: `app/ui/theme/` y `app/ui/components/`.

## Identidad

- **Nombre comercial en lobby:** JACKPOT PUZZLES
- **Estética:** casino premium oscuro, neón púrpura + oro, paneles glass, glow en jackpots
- **Modo:** siempre oscuro (`#131313` background)

## Paleta principal

| Token | Hex | Uso |
|-------|-----|-----|
| `primary` | `#EEC065` | Oro, créditos, CTAs, títulos destacados |
| `secondary` | `#DFB7FF` | Acento lavanda, bordes suaves |
| `secondary-container` | `#9D05FF` | Púrpura eléctrico, glow, nav inferior |
| `tertiary` | `#FFACE8` | VIP, barras de progreso, avatar |
| `tertiary-container` | `#FF52E3` | Badges premium |
| `surface` / `background` | `#131313` | Fondo base |
| `surface-container-high` | `#2A2A2A` | Chips (créditos) |
| `on-surface` | `#E5E2E1` | Texto principal |
| `on-surface-variant` | `#D2C5B2` | Texto secundario |

Tokens completos en `Color.kt`.

## Tipografía (Google Fonts)

| Estilo | Familia | Peso | Tamaño | Uso |
|--------|---------|------|--------|-----|
| `display-mobile` | Anybody | 800 | 36px | Jackpot, hero |
| `headline-md` | Anybody | 700 | 24px | Títulos de sección |
| `body-lg` / `body-sm` | Hanken Grotesk | 400 | 18/14px | Cuerpo |
| `label-caps` | Space Grotesk | 600 | 12px | Labels, nav, badges |
| `game-tile` | Space Grotesk | 700 | 20px | Tiles de juego |

## Espaciado y radios

- `container-padding`: 16dp
- `stack-gap`: 12dp
- `grid-gutter`: 8dp
- Radios: 4 / 8 / 12 / 16 / 24dp, pill = full

## Efectos de marca

- **Glass panel:** fondo `rgba(255,255,255,0.03)`, borde oro 10% alpha, blur
- **Neon gold:** sombra `#EEC065` — cards populares, jackpot
- **Neon purple:** sombra `#9D05FF` — cards nuevas / word slots
- **Jackpot pulse:** animación 2s en texto del mega jackpot

## Componentes Compose

| Componente | Descripción |
|------------|-------------|
| `LobbyScreen` | Pantalla hub principal (mockup HTML) |
| `DailyJackpotBanner` | Banner mega jackpot + SPIN TO WIN |
| `FeaturedGameCard` | Card bento con imagen, badge, glow |
| `VipStatusCard` | Rango + barra next level |
| `CreditsChip` | Contador de créditos (CR) |
| `SkillSlotBottomBar` | Nav inferior: Lobby / Word Slots / Sudoku / Vault |
| `GlassPanel` | Panel cristal reutilizable |
| `SkillSlotButton` | CTA pill dorado |

## Navegación (tabs)

| Tab | Ruta | Contenido |
|-----|------|-----------|
| Lobby | `lobby` | Hub principal |
| Word Slots | `slot` | Tragamonedas |
| Sudoku | `puzzle` | Shell de puzzle |
| Vault | `progression` | Mapa de progresión |

## Assets de referencia (lobby)

Imágenes de cards en el HTML original — URLs en `LobbyScreen.kt` (Coil).

---

*Fuente: diseño HTML lobby proporcionado por el equipo. Actualizar este doc si cambia la guía de marca.*
