# SkillSlot — Publicación en Play Store

Checklist orientativo para publicar la app en Google Play.

## Requisitos previos

- Cuenta de [Google Play Console](https://play.google.com/console) (~25 USD, pago único)
- Keystore de firma de release (guardar en lugar seguro)
- Política de privacidad publicada en URL accesible (obligatorio por AdMob)

## Assets necesarios

| Asset | Especificación |
|-------|----------------|
| Icono | 512×512 PNG |
| Feature graphic | 1024×500 |
| Capturas | Mín. 2 por tipo de dispositivo (teléfono 16:9) |
| Descripción corta | ≤ 80 caracteres |
| Descripción completa | ≤ 4000 caracteres |

## Textos sugeridos

**Título:** SkillSlot — Jackpot Puzzles

**Descripción corta:** Tragamonedas + 10 puzzles. Gira, desbloquea y sube de tier.

**Categoría:** Puzzle / Casual (sin dinero real)

**Clasificación de contenido:** PEGI 3 / Everyone — sin apuestas con dinero real

## Configuración técnica

1. Crear producto IAP `skillslot_premium` (compra única) en Play Console
2. Reemplazar IDs de prueba de AdMob por IDs de producción en `MonetizationConstants.kt`
3. Configurar `google-services.json` si se migra el ranking a Firebase (fase futura)
4. Activar ProGuard en release (`isMinifyEnabled = true`) tras verificar reglas
5. Generar AAB: `.\gradlew.bat :app:bundleRelease`

## Privacidad y cumplimiento

- Declarar uso de Advertising ID (AdMob)
- UMP / consentimiento GDPR para usuarios en EEE
- La versión actual guarda el ranking **localmente** en el dispositivo

## Notas de versión (ejemplo)

- 10 puzzles jugables con progresión por tiers
- Tragamonedas con desbloqueo de puzzles
- Modo Premium: sin anuncios y guardado de progreso
- Ranking local de puntuaciones
