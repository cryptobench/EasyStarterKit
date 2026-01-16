package com.easystarterkit.commands;

import com.easystarterkit.EasyStarterKit;
import com.easystarterkit.config.StarterKitConfig.ItemData;
import com.easystarterkit.config.StarterKitConfig.StarterKitData;
import com.easystarterkit.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class StarterKitCommand extends AbstractPlayerCommand {

    private final EasyStarterKit plugin;

    public StarterKitCommand(EasyStarterKit plugin) {
        super("easystarterkit", "Manage the starter kit for new players");
        this.plugin = plugin;
        setAllowsExtraArguments(true);
        requirePermission("starterkit.admin");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        String[] args = ctx.getInputString().trim().split("\\s+");

        if (args.length < 2) {
            showHelp(playerData);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "set" -> handleSet(store, playerRef, playerData);
            case "give" -> handleGive(store, playerRef, playerData);
            case "reset" -> handleReset(playerData);
            default -> showHelp(playerData);
        }
    }

    private void showHelp(PlayerRef playerData) {
        playerData.sendMessage(Messages.info("EasyStarterKit Commands:"));
        playerData.sendMessage(Messages.gray("  /easystarterkit set - Save your inventory as the starter kit"));
        playerData.sendMessage(Messages.gray("  /easystarterkit give - Give yourself the starter kit"));
        playerData.sendMessage(Messages.gray("  /easystarterkit reset - Reset your 'received kit' status"));
    }

    private void handleSet(Store<EntityStore> store, Ref<EntityStore> playerRef, PlayerRef playerData) {
        Player player = store.getComponent(playerRef, Player.getComponentType());
        Inventory inventory = player.getInventory();

        // Create a fresh snapshot of the current inventory
        StarterKitData kitData = new StarterKitData();
        kitData.savedAt = System.currentTimeMillis();

        // Copy only inventory items (storage + hotbar), not equipped items
        int totalItems = 0;
        totalItems += copySection(inventory.getStorage(), kitData.storage);
        totalItems += copySection(inventory.getHotbar(), kitData.hotbar);

        // Save the snapshot
        plugin.getConfig().setData(kitData);

        if (totalItems == 0) {
            playerData.sendMessage(Messages.warning("Starter kit cleared - your inventory was empty."));
        } else {
            playerData.sendMessage(Messages.success("Starter kit saved! " + totalItems + " item(s) will be given to new players."));
        }
    }

    private void handleGive(Store<EntityStore> store, Ref<EntityStore> playerRef, PlayerRef playerData) {
        if (!plugin.getConfig().hasKit()) {
            playerData.sendMessage(Messages.error("No starter kit has been set yet. Use /easystarterkit set first."));
            return;
        }

        Player player = store.getComponent(playerRef, Player.getComponentType());
        Inventory inventory = player.getInventory();
        StarterKitData kitData = plugin.getConfig().getData();

        // Give items to inventory (storage + hotbar only)
        int itemsGiven = 0;
        itemsGiven += giveItems(kitData.storage, inventory.getStorage());
        itemsGiven += giveItems(kitData.hotbar, inventory.getHotbar());

        playerData.sendMessage(Messages.success("Gave you " + itemsGiven + " item(s) from the starter kit."));
    }

    private void handleReset(PlayerRef playerData) {
        plugin.getPlayerDataManager().resetPlayer(playerData.getUuid());
        playerData.sendMessage(Messages.success("Your 'received kit' status has been reset. You'll receive the kit on next join."));
    }

    /**
     * Copies all items from a container section into the item list.
     */
    private int copySection(ItemContainer container, List<ItemData> targetList) {
        List<ItemData> items = new ArrayList<>();

        container.forEach((slotIndex, itemStack) -> {
            if (itemStack != null && !itemStack.isEmpty()) {
                ItemData itemData = new ItemData(
                    itemStack.getItemId(),
                    itemStack.getQuantity(),
                    slotIndex,
                    itemStack.getDurability(),
                    itemStack.getMaxDurability()
                );
                items.add(itemData);
            }
        });

        targetList.addAll(items);
        return items.size();
    }

    /**
     * Gives items from the saved kit data to the player's inventory container.
     */
    private int giveItems(List<ItemData> items, ItemContainer container) {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (ItemData itemData : items) {
            ItemStack itemStack = new ItemStack(
                itemData.itemId,
                itemData.quantity,
                itemData.durability,
                itemData.maxDurability,
                null
            );

            var transaction = container.addItemStackToSlot(itemData.slot, itemStack);
            if (transaction.succeeded()) {
                count++;
            } else {
                var fallbackTransaction = container.addItemStack(itemStack);
                if (fallbackTransaction.succeeded()) {
                    count++;
                }
            }
        }
        return count;
    }
}
