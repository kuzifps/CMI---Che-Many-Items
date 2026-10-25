package com.fxfps.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Botón y píldora estándar Neo-Capsule Liquid Glass para CheManyItems (CMI) 🇦🇷.
 *
 * Implementa estrictamente:
 * - Cápsula redondeada real (rounded-full, radius = h / 2).
 * - Pestaña activa: Resplandor Neón Cyan (#06b6d4).
 * - Heroico: Gradiente Esmeralda (#10b981 a #059669) con specular highlight.
 * - Acción: Ámbar cálido (#f59e0b a #d97706).
 * - Peligro: Carmesí vibrante (#e11d48).
 * - Cero controles grises vanilla.
 */
public class CmiButton extends Button {

    public enum Variant {
        DEFAULT,
        PRIMARY,
        GOLD,
        SUCCESS,
        DANGER,
        TAB,
        CHIP,
        HERO_EMERALD,
        ACTION_AMBER
    }

    private final Variant variant;
    private boolean selected = false;
    private int customHoverColor = -1;
    private boolean isToggle = false;
    private boolean toggleState = false;
    private Identifier iconTexture = null;
    private int animatedPresetId = -1;
    private int iconWidth = 16;
    private int iconHeight = 16;

    public CmiButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Variant variant) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.variant = (variant != null) ? variant : Variant.DEFAULT;
    }

    public CmiButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
        this(x, y, width, height, message, onPress, Variant.DEFAULT);
    }

    public void setAnimatedPresetId(int id) {
        this.animatedPresetId = id;
    }

    public int getAnimatedPresetId() {
        return this.animatedPresetId;
    }

    public void setIconTexture(Identifier iconTexture) {
        setIconTexture(iconTexture, 16, 16);
    }

    public void setIconTexture(Identifier iconTexture, int width, int height) {
        this.iconTexture = iconTexture;
        this.iconWidth = width;
        this.iconHeight = height;
    }

    public Identifier getIconTexture() {
        return this.iconTexture;
    }

    public void setToggle(boolean isToggle, boolean toggleState) {
        this.isToggle = isToggle;
        this.toggleState = toggleState;
    }

    public boolean isToggle() {
        return this.isToggle;
    }

    public boolean getToggleState() {
        return this.toggleState;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setCustomHoverColor(int color) {
        this.customHoverColor = color;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public static Builder cmiBuilder(Component message, Button.OnPress onPress) {
        return new Builder(message, onPress);
    }

    public static class Builder {
        private final Component message;
        private final Button.OnPress onPress;
        private int x = 0;
        private int y = 0;
        private int width = 150;
        private int height = 20;
        private Variant variant = Variant.DEFAULT;
        private boolean selected = false;

        public Builder(Component message, Button.OnPress onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder variant(Variant variant) {
            this.variant = variant;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public CmiButton build() {
            CmiButton btn = new CmiButton(x, y, width, height, message, onPress, variant);
            btn.setSelected(selected);
            return btn;
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = this.getHeight();

        boolean hovered = this.isHovered();
        boolean disabled = !this.active;
        long time = System.currentTimeMillis();

        int bgColor;
        int borderColor;
        int textColor;
        boolean isGradient = false;
        int gradTop = 0;
        int gradBottom = 0;

        if (disabled) {
            bgColor = 0x300D1017;
            borderColor = 0x20181C26;
            textColor = 0xFF4A5568;
        } else if (selected || (variant == Variant.TAB && selected)) {
            // Pestaña Activa con resplandor Cyan (#06b6d4)
            bgColor = 0xB006B6D4;
            borderColor = CmiUiTheme.COLOR_CYAN_NEON;
            textColor = 0xFFFFFFFF;
        } else {
            switch (variant) {
                case HERO_EMERALD, SUCCESS -> {
                    isGradient = true;
                    gradTop = hovered ? 0xFF059669 : 0xFF10B981;
                    gradBottom = hovered ? 0xFF047857 : 0xFF059669;
                    bgColor = gradTop;
                    borderColor = hovered ? 0xFF6EE7B7 : 0xFF34D399;
                    textColor = 0xFFFFFFFF;
                }
                case ACTION_AMBER, GOLD -> {
                    isGradient = true;
                    gradTop = hovered ? 0xFFD97706 : 0xFFF59E0B;
                    gradBottom = hovered ? 0xFFB45309 : 0xFFD97706;
                    bgColor = gradTop;
                    borderColor = hovered ? 0xFFFDE68A : 0xFFFCD34D;
                    textColor = 0xFFFFFFFF;
                }
                case DANGER -> {
                    isGradient = true;
                    gradTop = hovered ? 0xFFBE123C : 0xFFE11D48;
                    gradBottom = hovered ? 0xFF9F1239 : 0xFFBE123C;
                    bgColor = gradTop;
                    borderColor = hovered ? 0xFFFDA4AF : 0xFFFB7185;
                    textColor = 0xFFFFFFFF;
                }
                case TAB -> {
                    bgColor = hovered ? 0x601C2C46 : 0x350A1424;
                    borderColor = hovered ? CmiUiTheme.COLOR_CYAN_NEON : 0x5038BDF8;
                    textColor = hovered ? 0xFFFFFFFF : 0xFFCBD5E1;
                }
                case CHIP -> {
                    bgColor = hovered ? 0x651A263C : 0x350E182A;
                    borderColor = hovered ? CmiUiTheme.COLOR_CYAN_NEON : 0x4538BDF8;
                    textColor = hovered ? CmiUiTheme.COLOR_CYAN_NEON : 0xFFCCD6E0;
                }
                case PRIMARY -> {
                    bgColor = hovered ? 0x900284C7 : 0x650284C7;
                    borderColor = hovered ? 0xFF7DD3FC : CmiUiTheme.COLOR_CYAN_NEON;
                    textColor = 0xFFFFFFFF;
                }
                default -> { // DEFAULT
                    bgColor = hovered ? 0x551C263A : 0x30101828;
                    borderColor = hovered ? CmiUiTheme.COLOR_CYAN_NEON : 0x4038BDF8;
                    textColor = hovered ? 0xFFFFFFFF : 0xFFCCD6E0;
                }
            }
        }

        // Color animado para tags
        int animCol = -1;
        if (animatedPresetId >= 0) {
            animCol = com.fxfps.cmi.client.animated.AnimatedTagRegistry.getAnimatedColor(animatedPresetId, x, y);
            textColor = animCol;
            if (hovered) {
                borderColor = animCol;
            }
        }

        if (hovered && customHoverColor != -1 && !disabled) {
            borderColor = customHoverColor;
        }

        // Pulso sutil en hover
        if (!disabled && (hovered || selected)) {
            float pulse = (float) (Math.sin(time * 0.005) * 0.35 + 0.65);
            borderColor = CmiUiTheme.lerpColor(borderColor, 0xFFFFFFFF, pulse * 0.25f);
        }

        // 1. Curvatura real de cápsula (radio = h / 2 para rounded-full)
        int radius = Math.min(h / 2, Math.max(3, w / 4));
        if (isGradient) {
            CmiUiTheme.fillGradientRoundedRect(guiGraphics, x, y, w, h, radius, gradTop, gradBottom);
        } else {
            CmiUiTheme.fillRoundedRect(guiGraphics, x, y, w, h, radius, bgColor);
        }

        // 2. Specular highlight superior (1px)
        if (!disabled) {
            guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, (selected || hovered) ? 0x60FFFFFF : 0x20FFFFFF);
        }

        // 3. Borde con curvatura real continua
        CmiUiTheme.drawRoundedBorder(guiGraphics, x, y, w, h, radius, borderColor);

        // 4. Barra indicadora para pestaña activa
        if (variant == Variant.TAB && selected) {
            guiGraphics.fill(x + radius, y + h - 2, x + w - radius, y + h, 0xFF00E5FF);
        }

        // 5. Renderizado de contenido (icono, toggle o texto centrado)
        if (iconTexture != null) {
            int iconX = x + (w - iconWidth) / 2;
            int iconY = y + (h - iconHeight) / 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, iconTexture, iconX, iconY, 0f, 0f, iconWidth, iconHeight, iconWidth, iconHeight);
        } else {
            Font font = Minecraft.getInstance().font;
            Component msg = this.getMessage();
            int textY = y + (h - 8) / 2;

            if (animatedPresetId >= 0 && animCol != -1) {
                guiGraphics.fill(x + 4, y + (h - 6) / 2, x + 7, y + (h + 6) / 2, animCol);
                guiGraphics.centeredText(font, msg, x + (w + 5) / 2, textY, textColor);
            } else if (isToggle) {
                guiGraphics.text(font, msg, x + 8, textY, textColor);
                int toggleX = x + w - 30;
                int toggleY = y + (h - 13) / 2;
                CmiUiTheme.drawCmiToggle(guiGraphics, toggleX, toggleY, toggleState, hovered);
            } else {
                guiGraphics.centeredText(font, msg, x + w / 2, textY, textColor);
            }
        }
    }
}
