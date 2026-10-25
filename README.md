# 🇦🇷 CMI - CheManyItems

**CheManyItems (CMI)** es un mod client-side de última generación para **Minecraft Fabric 26.1.2** (Java 25, Mojang Mappings nativos). Diseñado con la arquitectura visual **Neo-Capsule Liquid Glass**, permite la creación ultrarrápida de kits de combate, edición avanzada de ítems, soporte de encantamientos sin límites (32k), lores multilínea en vivo, presets de shaders animados de servidor (`<rainbow>`, `<celestial>`, etc.), inspector visual in-game y hot-reload en tiempo real sin reiniciar el juego.

---

## ✨ Características Principales

### 🔮 1. Interfaz Neo-Capsule Liquid Glass
- **Mundo y Shaders en Vivo:** El juego **no se pausa** (`isPauseScreen -> false`) y el fondo es completamente transparente para mantener 60+ FPS fluidos con shaders activos.
- **Card Modal Flotante y Responsive:** Proporciones calculadas dinámicamente (`winW`, `winH`) adaptadas a cualquier GUI Scale (Auto, Normal, Grande).
- **Layout en 3 Columnas:**
  - **Columna 1 (Showcase & Vista Previa):** Pedestal circular 3D flotante con contador `x1`, previsualización en vivo del lore compilado, toggle de **Irrompible** y stepper numérico `[-] [ 1 ] [+] [64] [✕]`.
  - **Columna 2 (Edición de Nombre y Shaders):** Input de nombre, acción rápida `⚡ /rename`, utilidades `🛡 SmallCaps` y `😜 Emojis`, cuadrícula completa de presets animados (`<rainbow>`, `<celestial>`, `<fire>`, `<ocean>`, `<gold>`, `<toxic>`, `<matrix>`, etc.) y selector de gradientes HEX/RGB.
  - **Columna 3 (Lore y Paleta de Colores):** Paleta completa de 16 colores vanilla (`0-9`, `a-f`) y códigos de formato (`B`, `I`, `U`, `S`, `K`, `R`), editor de lore multilínea con badges rápidos (`<enchants>`, `<customenchants>`, `<attributes>`, `★ Star`) y botonera de acción (`✔ Sig.`, `📋 AutoLore`, `✕ Limpiar`, `✨ Limpio`).
- **Footer de Acciones:** Acceso directo a `🎒 Kits & Shulker`, `🎲 Gamble`, `🔄 Restablecer`, `📋 Copiar /give` y botón heroico esmeralda `✔ APLICAR EN SLOT`.

---

### 🛠️ 2. Motor de UI Hot-Reload e Inspector In-Game
- **`F6` - Inspector Visual In-Game:**
  - Visualiza hitboxes, bounding boxes y líneas guía proyectadas a los bordes con distancias en píxeles.
  - HUD flotante con información de ID, coordenadas, dimensiones y variantes de tema.
  - **Live Tweaking:** Flechas de dirección para desplazar (1px o 5px con `Shift`), `Alt + Flechas` para redimensionar.
  - **`Ctrl + S`:** Guarda los cambios instantáneamente en `config/cmi/ui_layout.json`.
- **`F7` / `Ctrl + R` - Hot Reload en Vivo:**
  - Recarga al instante las posiciones y dimensiones desde `config/cmi/ui_layout.json` sin reiniciar Minecraft.
- **`ESC`:** Cierre suave hacia la pantalla anterior o el juego.

---

### ⚡ 3. Encantamientos y Kits de Combate
- **Encantamientos sin Límites (32k / Ilegales):** Aplica niveles desde 1 hasta 32,767.
- **Custom Enchants de Servidor:** Menú interactivo dedicado (`/ce` o `/cmi ce`) con los 17 encantamientos especiales de Gamble.
- **Kits & Shulker Packer:** Guarda inventarios completos, cárgalos al instante o empaquétalos dentro de una Shulker Box con `/kit packshulker`.
- **Compartir Kits:** Importa y exporta kits mediante códigos ligeros `cmi://...`.

---

### ⚙️ 4. Menú de Ajustes Dedicado (`CmiSettingsScreen`)
- Configuración de teclas con detección de 1 solo clic (`> Presiona una tecla... <`).
- Alternadores de **Safe-Mode Anti-Kick**, **Shulker Spy** y **Calculadora de Daño DPS**.
- Acceso rápido desde el botón `⚙` en la cabecera o mediante `/cmi settings`.

---

## ⌨️ Comandos

| Comando | Descripción |
| :--- | :--- |
| `/cmi` o `/cmi menu` | Abre el editor principal de ítems CMI. |
| `/cmi ce` o `/ce` | Abre el menú visual interactivo de Custom Enchants. |
| `/cmi customenchant <enchant> <nivel>` | Aplica un Custom Enchant al ítem en mano. |
| `/cmi settings` | Abre la pantalla de ajustes de teclas y opciones. |
| `/cmi enchant <enchant> <nivel>` | Aplica un encantamiento (soporta 32k). |
| `/cmi rename <nombre>` | Renombra el ítem (admite tags de colores y animaciones). |
| `/cmi rename --smallcaps <nombre>` | Renombra convirtiendo el texto a tipografía SmallCaps. |
| `/cmi lore paste` | Pega el contenido del portapapeles como Lore del ítem. |
| `/kit create <nombre>` | Guarda el inventario actual como un kit nuevo. |
| `/kit load <nombre>` | Carga un kit guardado en el inventario. |
| `/kit packshulker [color]` | Empaqueta el kit actual en una Shulker Box. |

---

## 🏗️ Compilación e Instalación

### Requisitos:
- **Java:** JDK 25 (ej. Azul Zulu 25)
- **Minecraft:** 26.1.2 (Mojang Mappings nativos)
- **Loader:** Fabric Loader `>= 0.16.10`

### Instalación Rápida (Lunar Client y Vanilla):
Ejecuta el script incluido en la raíz del proyecto:
```bat
actualizar_mod.bat
```
El script compilará automáticamente las fuentes con Java 25, empaquetará el `.jar` en `build/libs/cmi-1.0.0.jar` y lo copiará directamente a los directorios de mods de Lunar Client y `.minecraft`.

---

## 📂 Estructura del Proyecto

```
cmi/
├── src/main/java/com/fxfps/cmi/
│   ├── CmiMod.java                         # Inicializador principal Fabric
│   └── client/
│       ├── CmiClientMod.java               # Inicializador de cliente y registro de keybinds
│       ├── commands/                       # Comandos cliente (/cmi, /ce, /kit)
│       ├── config/                         # Gestión de settings.json y ui_layout.json
│       ├── animated/                       # Presets animados (<rainbow>, <celestial>, etc.)
│       ├── combat/                         # Calculadora de daño en vivo
│       ├── fonts/                          # Soporte SmallCaps, gradientes y emojis
│       ├── lores/                          # Gestor de templates de Lore
│       ├── shulker/                        # Empaquetado automático en Shulkers
│       └── gui/                            # Interfaces de usuario Neo-Capsule Liquid Glass
│           ├── CmiScreen.java              # Pantalla base abstracta con Inspector y Hot-Reload
│           ├── CmiEditorScreen.java        # Editor principal de ítems
│           ├── CmiCustomEnchantsScreen.java# Selector de Custom Enchants
│           ├── CmiKitsScreen.java          # Gestor de kits
│           ├── CmiSettingsScreen.java      # Pantalla de ajustes
│           ├── CmiInspectorOverlay.java    # Inspector visual in-game
│           └── CmiUiTheme.java             # Paleta de estilos y gradientes
├── src/main/resources/
│   ├── fabric.mod.json                     # Metadata del mod Fabric
│   └── assets/                             # Texturas, logos y fuentes
├── config/cmi/
│   └── ui_layout.json                      # Configuración de coordenadas y diseño editable en vivo
├── build.gradle                            # Configuración de compilación Fabric Loom
├── gradle.properties                       # Versiones de dependencias
├── settings.gradle                         # Configuración de proyecto
├── actualizar_mod.bat                      # Script de compilación e instalación automática
└── README.md
```
