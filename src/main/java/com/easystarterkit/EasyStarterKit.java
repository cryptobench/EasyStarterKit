package com.easystarterkit;

import com.easystarterkit.commands.StarterKitCommand;
import com.easystarterkit.config.StarterKitConfig;
import com.easystarterkit.data.PlayerDataManager;
import com.easystarterkit.listeners.PlayerJoinListener;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class EasyStarterKit extends JavaPlugin {

    private StarterKitConfig config;
    private PlayerDataManager playerDataManager;

    public EasyStarterKit(JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {
        this.config = new StarterKitConfig(getDataDirectory());
        this.playerDataManager = new PlayerDataManager(getDataDirectory());

        // Register commands (requires starterkit.admin permission)
        getCommandRegistry().registerCommand(new StarterKitCommand(this));

        // Register player join listener
        PlayerJoinListener joinListener = new PlayerJoinListener(this);
        getEventRegistry().registerGlobal(PlayerConnectEvent.class, joinListener::onPlayerConnect);
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
    }

    public StarterKitConfig getConfig() {
        return config;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
