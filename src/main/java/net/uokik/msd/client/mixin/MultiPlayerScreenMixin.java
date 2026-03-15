package net.uokik.msd.client.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.uokik.msd.client.MsdClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(MultiplayerScreen.class)
public abstract class MultiPlayerScreenMixin {

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    private ServerList serverList;

    @Inject(method = "removeEntry", at = @At("HEAD"), cancellable = true)
    private void onRemoveEntry(CallbackInfo ci) {
        if (!MsdClient.hasAnySelected()) return;

        List<Integer> sorted = new ArrayList<>(MsdClient.getSelectedIndexes());
        sorted.sort(Comparator.reverseOrder());

        for (int index : sorted) {
            if (index >= 0 && index < serverList.size()) {
                serverList.remove(serverList.get(index));
            }
        }

        serverList.saveFile();
        serverListWidget.setServers(serverList);
        MsdClient.clearSelected();
        ci.cancel();
    }
}
