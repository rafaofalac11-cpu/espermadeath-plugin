package com.example.pollosmagicos;

import org.bukkit.plugin.java.JavaPlugin;

public class PollosMagicos extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MagicChickenListener(this), this);
        getServer().getPluginManager().registerEvents(new HuevitoVidaListener(this), this);
        getCommand("pollosmagicos").setExecutor(new PollosMagicosCommand(this));
        getLogger().info("PollosMagicos activado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PollosMagicos desactivado.");
    }
}
