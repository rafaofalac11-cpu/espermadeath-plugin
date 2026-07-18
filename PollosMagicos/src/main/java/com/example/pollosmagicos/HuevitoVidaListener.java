package com.example.pollosmagicos;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HuevitoVidaListener implements Listener {

    private final PollosMagicos plugin;
    private final Set<UUID> activos = new HashSet<>();

    public HuevitoVidaListener(PollosMagicos plugin) {
        this.plugin = plugin;
        iniciarTareaEfectos();
    }

    private void iniciarTareaEfectos() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    boolean tieneHuevito = tieneHuevitoInventario(player);
                    UUID uuid = player.getUniqueId();

                    if (tieneHuevito) {
                        aplicarEfectos(player);
                        activos.add(uuid);
                    } else if (activos.contains(uuid)) {
                        quitarEfectos(player);
                        activos.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        activos.remove(event.getPlayer().getUniqueId());
    }

    private boolean tieneHuevitoInventario(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (HuevitoVidaItem.isHuevitoDeVida(item)) return true;
        }
        if (HuevitoVidaItem.isHuevitoDeVida(player.getInventory().getItemInOffHand())) return true;
        return false;
    }

    private void aplicarEfectos(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 1, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false, false));
    }

    private void quitarEfectos(Player player) {
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.SPEED);
    }
}
