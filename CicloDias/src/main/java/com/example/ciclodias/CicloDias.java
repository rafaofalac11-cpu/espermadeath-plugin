package com.example.ciclodias;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CicloDias extends JavaPlugin {

    private DiaCycleManager diaCycleManager;
    private DiasManager diasManager;
    private VillagerDiscount villagerDiscount;
    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadDataFile();

        diasManager = new DiasManager(this);

        if (getConfig().getBoolean("dia-extendido", true)) {
            diaCycleManager = new DiaCycleManager(this);
        }

        if (getConfig().getBoolean("descuento-aldeanos", true)) {
            double porcentaje = getConfig().getDouble("porcentaje-descuento", 25.0);
            villagerDiscount = new VillagerDiscount(this, porcentaje);
        }

        DiasCommand command = new DiasCommand(this);
        getCommand("dias").setExecutor(command);
        getCommand("dias").setTabCompleter(command);

        getLogger().info("CicloDias activado. Dia actual: " + diasManager.getDiaActual());
    }

    @Override
    public void onDisable() {
        saveDataFile();
        getLogger().info("CicloDias desactivado.");
    }

    private void loadDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("No se pudo crear data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("No se pudo guardar data.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getDataStore() {
        return dataConfig;
    }

    public void saveData() {
        saveDataFile();
    }

    public DiasManager getDiasManager() {
        return diasManager;
    }
}
