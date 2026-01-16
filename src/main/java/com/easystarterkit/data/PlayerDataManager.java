package com.easystarterkit.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path dataFile;
    private Set<UUID> receivedKitPlayers;

    public PlayerDataManager(Path dataDirectory) {
        this.dataFile = dataDirectory.resolve("players.json");
        load();
    }

    private void load() {
        if (Files.exists(dataFile)) {
            try {
                String json = Files.readString(dataFile);
                this.receivedKitPlayers = GSON.fromJson(json, new TypeToken<Set<UUID>>(){}.getType());
                if (this.receivedKitPlayers == null) {
                    this.receivedKitPlayers = new HashSet<>();
                }
            } catch (IOException e) {
                this.receivedKitPlayers = new HashSet<>();
            }
        } else {
            this.receivedKitPlayers = new HashSet<>();
        }
    }

    public void save() {
        try {
            Files.createDirectories(dataFile.getParent());
            Files.writeString(dataFile, GSON.toJson(receivedKitPlayers));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasReceivedKit(UUID playerId) {
        return receivedKitPlayers.contains(playerId);
    }

    public void markAsReceived(UUID playerId) {
        receivedKitPlayers.add(playerId);
        save();
    }

    public void resetPlayer(UUID playerId) {
        receivedKitPlayers.remove(playerId);
        save();
    }
}
