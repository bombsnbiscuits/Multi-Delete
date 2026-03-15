package net.uokik.msd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;

import java.util.*;

public class MsdClient implements ClientModInitializer {
    private static final Set<Integer> selectedIndexes = new HashSet<>();
    public static final int CB_SIZE = 11;

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof MultiplayerScreen multiplayerScreen)) {
                return;
            }

            clearSelected();

            ButtonWidget[] deleteButton = new ButtonWidget[1];
            String deleteLabel = Text.translatable("selectServer.delete").getString();

            for (var element : screen.children()) {
                if (element instanceof ButtonWidget btn) {
                    if (btn.getMessage().getString().equals(deleteLabel)) {
                        deleteButton[0] = btn;
                        break;
                    }
                }
            }

            ScreenEvents.afterRender(screen).register((scr, drawContext, mouseX, mouseY, tickDelta) -> {
                ButtonWidget btn = deleteButton[0];
                if (btn != null && hasAnySelected()) {
                    btn.active = true;
                }
            });

            ScreenMouseEvents.allowMouseClick(screen).register((scr, click) -> {
                if (click.button() != 0) return true;

                double mouseX = click.x();
                double mouseY = click.y();

                MultiplayerServerListWidget serverListWidget = multiplayerScreen.serverListWidget;

                for (int i = 0; i < serverListWidget.children().size(); i++) {
                    var entry = serverListWidget.children().get(i);
                    if (!(entry instanceof MultiplayerServerListWidget.ServerEntry)) {
                        continue;
                    }

                    int cbX = entry.getContentX() - CB_SIZE - 4;
                    int cbY = entry.getContentY() + entry.getContentHeight() / 2 - 5;

                    if (mouseX >= cbX && mouseX <= cbX + CB_SIZE
                            && mouseY >= cbY && mouseY <= cbY + CB_SIZE) {
                        toggleSelected(i);
                        return false;
                    }
                }

                ButtonWidget btn = deleteButton[0];
                if (btn != null && hasAnySelected()) {
                    if (mouseX >= btn.getX() && mouseX <= btn.getX() + btn.getWidth()
                            && mouseY >= btn.getY() && mouseY <= btn.getY() + btn.getHeight()) {
                        deleteSelected(multiplayerScreen);
                        return false;
                    }
                }

                return true;
            });
        });
    }

    private static void deleteSelected(MultiplayerScreen screen) {
        ServerList serverList = screen.getServerList();
        MultiplayerServerListWidget serverListWidget = screen.serverListWidget;

        List<Integer> sorted = new ArrayList<>(getSelectedIndexes());
        sorted.sort(Comparator.reverseOrder());

        for (int index : sorted) {
            if (index >= 0 && index < serverList.size()) {
                serverList.remove(serverList.get(index));
            }
        }

        serverList.saveFile();
        serverListWidget.setServers(serverList);
        clearSelected();
    }

    public static boolean isSelected(int index) {
        return selectedIndexes.contains(index);
    }

    public static void toggleSelected(int index) {
        if (!selectedIndexes.remove(index)) {
            selectedIndexes.add(index);
        }
    }

    public static void clearSelected() {
        selectedIndexes.clear();
    }

    public static boolean hasAnySelected() {
        return !selectedIndexes.isEmpty();
    }

    public static Set<Integer> getSelectedIndexes() {
        return new HashSet<>(selectedIndexes);
    }
}
