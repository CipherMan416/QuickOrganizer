package com.cipherman.quickorganizer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.lwjgl.glfw.GLFW;

public class QuickOrganizerClient implements ClientModInitializer {
    private static KeyBinding sortKeyBinding;

    @Override
    public void onInitializeClient() {
        sortKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickorganizer.sort",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.quickorganizer"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (sortKeyBinding.wasPressed()) {
                sortInventory(client);
            }
        });

        QuickOrganizerMod.LOGGER.info("QuickOrganizer client initialized! Press R to sort inventory.");
    }

    private void sortInventory(MinecraftClient client) {
        if (client.player == null) return;

        ScreenHandler handler = client.player.currentScreenHandler;
        if (!(handler instanceof PlayerScreenHandler)) return;

        // 收集所有物品
        java.util.List<ItemStack> items = new java.util.ArrayList<>();
        PlayerScreenHandler inventory = (PlayerScreenHandler) handler;

        // 收集主背包物品 (slot 9-35)
        for (int i = 9; i < 36; i++) {
            ItemStack stack = inventory.getSlot(i).getStack();
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                stack.setCount(0);
            }
        }

        // 按物品名称排序
        items.sort((a, b) -> {
            String nameA = a.getName().getString();
            String nameB = b.getName().getString();
            return nameA.compareToIgnoreCase(nameB);
        });

        // 重新放入背包
        int slot = 9;
        for (ItemStack item : items) {
            while (item.getCount() > 0 && slot < 36) {
                ItemStack existing = inventory.getSlot(slot).getStack();
                if (existing.isEmpty()) {
                    int canPlace = Math.min(item.getMaxCount(), item.getCount());
                    ItemStack toPlace = item.copy();
                    toPlace.setCount(canPlace);
                    inventory.getSlot(slot).setStack(toPlace);
                    item.decrement(canPlace);
                    slot++;
                } else if (ItemStack.areItemsEqual(existing, item)) {
                    int canStack = existing.getMaxCount() - existing.getCount();
                    int toMove = Math.min(canStack, item.getCount());
                    existing.increment(toMove);
                    item.decrement(toMove);
                    if (item.getCount() <= 0) slot++;
                } else {
                    slot++;
                }
            }
        }

        client.player.closeHandledScreen();
    }
}
