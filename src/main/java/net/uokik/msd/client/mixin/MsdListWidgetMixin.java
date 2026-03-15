package net.uokik.msd.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.uokik.msd.client.MsdClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MsdListWidgetMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCheckbox(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        int cbX = y - MsdClient.CB_SIZE - 4;
        int cbY = x + entryWidth / 2 - 5;

        boolean selected = MsdClient.isSelected(index);
        int borderColor = selected ? 0xFF4488CC : 0xFF888888;

        // Border
        context.fill(cbX - 1, cbY - 1, cbX + MsdClient.CB_SIZE + 1, cbY + MsdClient.CB_SIZE + 1, borderColor);
        // Background
        context.fill(cbX, cbY, cbX + MsdClient.CB_SIZE, cbY + MsdClient.CB_SIZE, 0xFF19191A);

        if (selected) {
            context.drawText(MinecraftClient.getInstance().textRenderer, "\u2713", cbX + 2, cbY + 1, 0xFF4488CC, true);
        }
    }
}
