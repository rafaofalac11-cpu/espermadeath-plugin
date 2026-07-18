package com.permadeath.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PermaDeath extends JavaPlugin {

    private File eliminatedFile;
    private FileConfiguration eliminatedConfig;
    private final Set<UUID> eliminados = new HashSet<>();

    @Override
    public void onEnable() {
        cargarEliminados();
        getServer().getPluginManager().registerEvents(new PermadeathListener(this), this);
        getCommand("revivir").setExecutor(new RevivirCommand(this));
        getLogger().info("PermaDeath activado. Jugadores eliminados actualmente: " + eliminados.size());
    }

    @Override
    public void onDisable() {
        guardarEliminados();
    }

    private void cargarEliminados() {
        eliminatedFile = new File(getDataFolder(), "eliminados.yml");
        if (!eliminatedFile.exists()) {
            getDataFolder().mkdirs();
            try {
                eliminatedFile.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "No se pudo crear eliminados.yml", e);
            }
        }
        eliminatedConfig = YamlConfiguration.loadConfiguration(eliminatedFile);
        for (String uuidStr : eliminatedConfig.getStringList("eliminados")) {
            try {
                eliminados.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void guardarEliminados() {
        eliminatedConfig.set("eliminados", eliminados.stream().map(UUID::toString).toList());
        try {
            eliminatedConfig.save(eliminatedFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "No se pudo guardar eliminados.yml", e);
        }
    }

    public void marcarEliminado(UUID uuid) {
        eliminados.add(uuid);
        guardarEliminados();
    }

    public void quitarEliminado(UUID uuid) {
        eliminados.remove(uuid);
        guardarEliminados();
    }

    public boolean estaEliminado(UUID uuid) {
        return eliminados.contains(uuid);
    }
}
