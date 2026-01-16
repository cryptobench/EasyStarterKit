package com.easystarterkit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StarterKitConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configFile;
    private StarterKitData data;

    public StarterKitConfig(Path dataDirectory) {
        this.configFile = dataDirectory.resolve("starterkit.json");
        load();
    }

    private void load() {
        if (Files.exists(configFile)) {
            try {
                String json = Files.readString(configFile);
                this.data = GSON.fromJson(json, StarterKitData.class);
                if (this.data == null) {
                    this.data = new StarterKitData();
                }
            } catch (IOException e) {
                this.data = new StarterKitData();
            }
        } else {
            this.data = new StarterKitData();
        }
    }

    public void save() {
        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, GSON.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StarterKitData getData() {
        return data;
    }

    public void setData(StarterKitData data) {
        this.data = data;
        save();
    }

    public boolean hasKit() {
        if (data == null) return false;
        return hasItems(data.storage) || hasItems(data.hotbar) ||
               hasItems(data.armor) || hasItems(data.utility) ||
               hasItems(data.tools) || hasItems(data.backpack);
    }

    private boolean hasItems(List<ItemData> items) {
        return items != null && !items.isEmpty();
    }

    /**
     * Inventory section types matching Hytale's inventory structure
     */
    public enum InventorySection {
        STORAGE,
        HOTBAR,
        ARMOR,
        UTILITY,
        TOOLS,
        BACKPACK
    }

    /**
     * Complete starter kit data - a snapshot of all inventory sections
     */
    public static class StarterKitData {
        public List<ItemData> storage = new ArrayList<>();
        public List<ItemData> hotbar = new ArrayList<>();
        public List<ItemData> armor = new ArrayList<>();
        public List<ItemData> utility = new ArrayList<>();
        public List<ItemData> tools = new ArrayList<>();
        public List<ItemData> backpack = new ArrayList<>();
        public long savedAt; // Timestamp when kit was saved

        public List<ItemData> getSection(InventorySection section) {
            return switch (section) {
                case STORAGE -> storage;
                case HOTBAR -> hotbar;
                case ARMOR -> armor;
                case UTILITY -> utility;
                case TOOLS -> tools;
                case BACKPACK -> backpack;
            };
        }
    }

    /**
     * Represents a single item in the kit - an immutable snapshot
     */
    public static class ItemData {
        public String itemId;
        public int quantity;
        public short slot;
        public double durability;
        public double maxDurability;

        public ItemData() {}

        public ItemData(String itemId, int quantity, short slot, double durability, double maxDurability) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.slot = slot;
            this.durability = durability;
            this.maxDurability = maxDurability;
        }
    }
}
