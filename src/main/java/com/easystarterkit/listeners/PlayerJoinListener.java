package com.easystarterkit.listeners;

import com.easystarterkit.EasyStarterKit;
import com.easystarterkit.config.StarterKitConfig.ItemData;
import com.easystarterkit.config.StarterKitConfig.StarterKitData;
import com.easystarterkit.util.Messages;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener {

    private final EasyStarterKit plugin;

    public PlayerJoinListener(EasyStarterKit plugin) {
        this.plugin = plugin;
    }

    public void onPlayerConnect(PlayerConnectEvent event) {
        UUID playerId = event.getPlayerRef().getUuid();

        // Check if player already received the kit
        if (plugin.getPlayerDataManager().hasReceivedKit(playerId)) {
            return;
        }

        // Check if there's a kit to give
        if (!plugin.getConfig().hasKit()) {
            return;
        }

        // Get the saved kit data (immutable snapshot)
        StarterKitData kitData = plugin.getConfig().getData();

        // Get the player and their inventory
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        // Give items to inventory (storage + hotbar only)
        int itemsGiven = 0;
        itemsGiven += giveItems(kitData.storage, inventory.getStorage());
        itemsGiven += giveItems(kitData.hotbar, inventory.getHotbar());

        // Mark player as having received the kit
        plugin.getPlayerDataManager().markAsReceived(playerId);

        // Send welcome message if items were given
        if (itemsGiven > 0) {
            event.getPlayerRef().sendMessage(Messages.info("Welcome! You've received a starter kit with " + itemsGiven + " item(s)."));
        }
    }

    /**
     * Gives items from the saved kit data to the player's inventory container.
     * Creates new ItemStack instances from the saved snapshot data.
     */
    private int giveItems(List<ItemData> items, ItemContainer container) {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (ItemData itemData : items) {
            // Create a fresh ItemStack from the saved snapshot data
            ItemStack itemStack = new ItemStack(
                itemData.itemId,
                itemData.quantity,
                itemData.durability,
                itemData.maxDurability,
                null // No metadata for now
            );

            // Try to add to the original slot first
            var transaction = container.addItemStackToSlot(itemData.slot, itemStack);
            if (transaction.succeeded()) {
                count++;
            } else {
                // If original slot is taken, try to add anywhere in the container
                var fallbackTransaction = container.addItemStack(itemStack);
                if (fallbackTransaction.succeeded()) {
                    count++;
                }
            }
        }
        return count;
    }
}
