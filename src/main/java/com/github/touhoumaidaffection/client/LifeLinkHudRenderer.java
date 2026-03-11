package com.github.touhoumaidaffection.client;

import com.github.touhoumaidaffection.ModDataComponents;
import com.github.touhoumaidaffection.data.LifeLinkData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LifeLinkHudRenderer {

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack activeChain = null;
        if (mainHand.is(Items.CHAIN)) activeChain = mainHand;
        else if (offHand.is(Items.CHAIN)) activeChain = offHand;

        if (activeChain == null) return;

        LifeLinkData data = activeChain.get(ModDataComponents.LIFE_LINK.get());
        if (data == null || !data.hasBoundMaids()) return;

        // Determine heart color based on energy level
        int heartColor;
        if (data.currentEnergy() <= 0) {
            heartColor = 0x555555; // Gray when depleted
        } else if (data.currentEnergy() < data.maxEnergy() * 0.3f) {
            heartColor = 0xFF5555; // Light red when low
        } else {
            heartColor = 0xFF4444; // Red when normal
        }

        MutableComponent heart = Component.literal("\u2764 ").withStyle(Style.EMPTY.withColor(heartColor));
        MutableComponent numbers = Component.literal(
                String.format("%.1f / %.1f", data.currentEnergy(), data.maxEnergy())
        ).withStyle(Style.EMPTY.withColor(0xFFFFFF));
        MutableComponent text = heart.append(numbers);

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int textWidth = mc.font.width(text);

        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - 54;

        // Semi-transparent background
        guiGraphics.fill(x - 3, y - 2, x + textWidth + 3, y + 11, 0x80000000);
        guiGraphics.drawString(mc.font, text, x, y, 0xFFFFFF, true);
    }
}
