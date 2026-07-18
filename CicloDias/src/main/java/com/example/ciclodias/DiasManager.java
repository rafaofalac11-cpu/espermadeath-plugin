package com.example.ciclodias;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiasManager {

    private final CicloDias plugin;
    private int diaActual;

    public DiasManager(CicloDias plugin) {
        this.plugin = plugin;
        this.diaActual = plugin.getDataStore().getInt("dia-actual", 0);
    }

    public int getDiaActual() {
        return diaActual;
    }

    public void avanzarDia() {
        diaActual++;
        guardarDia();
        notificarCambio();
    }

    public void retrocederDia() {
        if (diaActual > 0) {
            diaActual--;
        } else {
            diaActual = 0;
        }
        guardarDia();
        notificarCambio();
    }

    public void setDia(int dia) {
        this.diaActual = dia;
        guardarDia();
        notificarCambio();
    }

    private void guardarDia() {
        plugin.getDataStore().set("dia-actual", diaActual);
        plugin.saveData();
    }

    private void notificarCambio() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "=== DIA " + diaActual + " ===");
            player.sendMessage("");
        }
    }
}
