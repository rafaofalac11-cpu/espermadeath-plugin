package com.example.ciclodias;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class DiaCycleManager {

    private final CicloDias plugin;
    private final int ticksNormales = 24000;
    private final int ticksExtendidos = 36000;

    public DiaCycleManager(CicloDias plugin) {
        this.plugin = plugin;
        iniciarTarea();
    }

    private void iniciarTarea() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                if (world == null) return;

                world.setGameRuleValue("doDaylightCycle", "false");

                long tiempoActual = world.getTime();
                long incremento = ticksExtendidos / ticksNormales;

                long nuevoTiempo = tiempoActual + incremento;
                if (nuevoTiempo >= 24000) {
                    nuevoTiempo = nuevoTiempo - 24000;
                }
                world.setTime(nuevoTiempo);
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
